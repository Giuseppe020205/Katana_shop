package com.katana.shop.service;

import com.katana.shop.Repository.OrdineRepository;
import com.katana.shop.Repository.RigaCarrelloRepository;
import com.katana.shop.Repository.UtenteRepository;
import com.katana.shop.dto.OrdineDto;
import com.katana.shop.exception.CarrelloNonAcquistabileException;
import com.katana.shop.model.Katana;
import com.katana.shop.model.Ordine;
import com.katana.shop.model.RigaCarrello;
import com.katana.shop.model.RigaOrdine;
import com.katana.shop.model.StatoOrdine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrdineService {

    public enum EsitoConferma {
        CONFERMATO,
        GIA_ELABORATO,
        DA_RIMBORSARE,
        SCONOSCIUTO
    }

    private final OrdineRepository ordineRepository;
    private final RigaCarrelloRepository rigaCarrelloRepository;
    private final UtenteRepository utenteRepository;

    public OrdineService(OrdineRepository ordineRepository,
                         RigaCarrelloRepository rigaCarrelloRepository,
                         UtenteRepository utenteRepository) {
        this.ordineRepository = ordineRepository;
        this.rigaCarrelloRepository = rigaCarrelloRepository;
        this.utenteRepository = utenteRepository;
    }

    @Transactional
    public OrdineDto creaOrdineDaCarrello(Long utenteId) {
        List<RigaCarrello> righeCarrello = rigaCarrelloRepository.findByUtenteIdOrderById(utenteId);
        if (righeCarrello.isEmpty()) {
            throw new CarrelloNonAcquistabileException("Il carrello è vuoto");
        }

        ordineRepository.findByUtenteIdAndStato(utenteId, StatoOrdine.IN_ATTESA_PAGAMENTO)
                .forEach(vecchio -> vecchio.setStato(StatoOrdine.ANNULLATO));

        Ordine ordine = new Ordine(utenteRepository.getReferenceById(utenteId));
        for (RigaCarrello riga : righeCarrello) {
            Katana katana = riga.getKatana();
            int disponibili = katana.getGiacenza_magazzino();
            if (disponibili <= 0) {
                throw new CarrelloNonAcquistabileException(
                        "La katana \"" + katana.getNome() + "\" non è più disponibile");
            }
            ordine.aggiungiRiga(katana, Math.min(riga.getQuantita(), disponibili));
        }
        return OrdineDto.from(ordineRepository.save(ordine));
    }

    @Transactional(readOnly = true)
    public List<String> sessioniInSospeso(Long utenteId) {
        return ordineRepository.findByUtenteIdAndStato(utenteId, StatoOrdine.IN_ATTESA_PAGAMENTO).stream()
                .map(Ordine::getStripeSessionId)
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional
    public void collegaSessione(Long ordineId, String stripeSessionId) {
        Ordine ordine = ordineRepository.findById(ordineId)
                .orElseThrow(() -> new IllegalStateException("Ordine non trovato: " + ordineId));
        ordine.setStripeSessionId(stripeSessionId);
    }

    @Transactional
    public void annulla(Long ordineId) {
        ordineRepository.findById(ordineId).ifPresent(o -> o.setStato(StatoOrdine.ANNULLATO));
    }

    @Transactional
    public EsitoConferma confermaPagamento(String stripeSessionId) {
        Optional<Ordine> trovato = ordineRepository.findByStripeSessionId(stripeSessionId);
        if (trovato.isEmpty()) {
            return EsitoConferma.SCONOSCIUTO;
        }
        Ordine ordine = trovato.get();

        if (ordine.getStato() == StatoOrdine.PAGATO) {
            return EsitoConferma.GIA_ELABORATO;
        }
        if (ordine.getStato() == StatoOrdine.ANNULLATO) {
            return EsitoConferma.DA_RIMBORSARE;
        }

        for (RigaOrdine riga : ordine.getRighe()) {
            if (riga.getKatana().getGiacenza_magazzino() < riga.getQuantita()) {
                ordine.setStato(StatoOrdine.ANNULLATO);
                return EsitoConferma.DA_RIMBORSARE;
            }
        }

        Long utenteId = ordine.getUtente().getId();
        for (RigaOrdine riga : ordine.getRighe()) {
            Katana katana = riga.getKatana();
            katana.setGiacenza_magazzino(katana.getGiacenza_magazzino() - riga.getQuantita());
            rigaCarrelloRepository.findByUtenteIdAndKatanaId(utenteId, katana.getId())
                    .ifPresent(rigaCarrelloRepository::delete);
        }
        ordine.setStato(StatoOrdine.PAGATO);
        return EsitoConferma.CONFERMATO;
    }

    @Transactional
    public void sessioneScaduta(String stripeSessionId) {
        ordineRepository.findByStripeSessionId(stripeSessionId)
                .filter(o -> o.getStato() == StatoOrdine.IN_ATTESA_PAGAMENTO)
                .ifPresent(o -> o.setStato(StatoOrdine.ANNULLATO));
    }
}