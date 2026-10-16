package com.katana.shop.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katana.shop.service.PagamentoService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagamenti")
public class StripeWebhookController {
    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final PagamentoService pagamentoService;
    private final ObjectMapper objectMapper;
    private final String webhookSecret;

    public StripeWebhookController(PagamentoService pagamentoService,
                                   ObjectMapper objectMapper,
                                   @Value("${app.stripe.webhook-secret}") String webhookSecret) {
        this.pagamentoService = pagamentoService;
        this.objectMapper = objectMapper;
        this.webhookSecret = webhookSecret;
        log.info("Webhook secret caricato: lunghezza {}, finisce con {}", webhookSecret.length(),
                webhookSecret.length() > 4 ? webhookSecret.substring(webhookSecret.length() - 4) : "-");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> riceviEvento(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String firma) {

        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.error("Webhook Stripe ricevuto, ma STRIPE_WEBHOOK_SECRET non è impostata");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("webhook non configurato");
        }
        if (firma == null) {
            return ResponseEntity.badRequest().body("firma mancante");
        }

        Event evento;
        try {
            evento = Webhook.constructEvent(payload, firma, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Webhook Stripe con firma non valida");
            return ResponseEntity.badRequest().body("firma non valida");
        }

        try {
            JsonNode sessione = objectMapper.readTree(payload).path("data").path("object");
            String sessionId = sessione.path("id").asText(null);

            switch (evento.getType()) {
                case "checkout.session.completed" -> {
                    if ("paid".equals(sessione.path("payment_status").asText())) {
                        pagamentoService.pagamentoCompletato(sessionId, sessione.path("payment_intent").asText(null));
                    }
                }
                case "checkout.session.expired" -> pagamentoService.sessioneScaduta(sessionId);
                default -> log.debug("Evento Stripe ignorato: {}", evento.getType());
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("payload non valido");
        }
        return ResponseEntity.ok("ok");
    }
}