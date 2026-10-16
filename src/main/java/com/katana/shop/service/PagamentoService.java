package com.katana.shop.service;

import com.katana.shop.dto.CheckoutResponse;
import com.katana.shop.dto.OrdineDto;
import com.katana.shop.dto.RigaOrdineDto;
import com.katana.shop.exception.PagamentoNonDisponibileException;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionExpireParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.param.RefundCreateParams;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
public class PagamentoService {
    private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);

    private final OrdineService ordineService;
    private final String chiaveSegreta;
    private final String frontendUrl;

    public PagamentoService(OrdineService ordineService,
                            @Value("${app.stripe.secret-key}") String chiaveSegreta,
                            @Value("${app.frontend-url}") String frontendUrl) {
        this.ordineService = ordineService;
        this.chiaveSegreta = chiaveSegreta;
        this.frontendUrl = frontendUrl;
    }

    public CheckoutResponse avviaCheckout(Long utenteId, String emailUtente) {
        if (chiaveSegreta == null || chiaveSegreta.isBlank()) {
            throw new PagamentoNonDisponibileException("Il servizio di pagamento non è configurato");
        }
        StripeClient client = new StripeClient(chiaveSegreta);

        List<String> vecchieSessioni = ordineService.sessioniInSospeso(utenteId);

        OrdineDto ordine = ordineService.creaOrdineDaCarrello(utenteId);

        Session sessione;
        try {
            sessione = client.v1().checkout().sessions().create(parametri(ordine, emailUtente));
        } catch (StripeException e) {
            log.error("Stripe: creazione della sessione non riuscita per l'ordine {}", ordine.id(), e);
            ordineService.annulla(ordine.id());
            throw new PagamentoNonDisponibileException("Impossibile avviare il pagamento, riprova tra poco");
        }

        ordineService.collegaSessione(ordine.id(), sessione.getId());
        vecchieSessioni.forEach(id -> chiudiSessione(client, id));
        return new CheckoutResponse(sessione.getUrl());
    }

    private SessionCreateParams parametri(OrdineDto ordine, String emailUtente) {
        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setClientReferenceId(String.valueOf(ordine.id()))
                .putMetadata("ordineId", String.valueOf(ordine.id()))
                .setCustomerEmail(emailUtente)
                .setExpiresAt(Instant.now().plusSeconds(3600).getEpochSecond())
                .setSuccessUrl(frontendUrl + "/pagamento/successo?ordine=" + ordine.id())
                .setCancelUrl(frontendUrl + "/pagamento/annullato?ordine=" + ordine.id());

        for (RigaOrdineDto riga : ordine.righe()) {
            builder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) riga.quantita())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("eur")
                                            .setUnitAmount(inCentesimi(riga.prezzoUnitario()))
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(riga.nome())
                                                            .build())
                                            .build())
                            .build());
        }
        return builder.build();
    }

    private long inCentesimi(BigDecimal importo) {
        return importo.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
    }

    private void chiudiSessione(StripeClient client, String sessionId) {
        try {
            client.v1().checkout().sessions().expire(sessionId, SessionExpireParams.builder().build());
        } catch (StripeException e) {
            log.info("Stripe: sessione {} non chiusa ({})", sessionId, e.getMessage());
        }
    }
    public void pagamentoCompletato(String sessionId, String paymentIntentId) {
        OrdineService.EsitoConferma esito = ordineService.confermaPagamento(sessionId);
        switch (esito) {
            case CONFERMATO -> log.info("Ordine pagato e confermato (sessione {})", sessionId);
            case GIA_ELABORATO -> log.info("Sessione {} già elaborata, nessuna azione", sessionId);
            case SCONOSCIUTO -> log.warn("Pagamento ricevuto per una sessione sconosciuta: {}", sessionId);
            case DA_RIMBORSARE -> rimborsa(paymentIntentId, sessionId);
        }
    }

    public void sessioneScaduta(String sessionId) {
        ordineService.sessioneScaduta(sessionId);
    }

    private void rimborsa(String paymentIntentId, String sessionId) {
        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            log.error("Impossibile rimborsare la sessione {}: manca il pagamento associato", sessionId);
            return;
        }
        try {
            new StripeClient(chiaveSegreta).v1().refunds().create(
                    RefundCreateParams.builder().setPaymentIntent(paymentIntentId).build());
            log.warn("Ordine annullato e pagamento rimborsato (sessione {})", sessionId);
        } catch (StripeException e) {
            if ("charge_already_refunded".equals(e.getCode())) {
                log.info("Pagamento già rimborsato (sessione {})", sessionId);
                return;
            }
            log.error("Stripe: rimborso non riuscito per la sessione {}", sessionId, e);
            throw new PagamentoNonDisponibileException("Rimborso non riuscito, verrà ritentato");
        }
    }
}