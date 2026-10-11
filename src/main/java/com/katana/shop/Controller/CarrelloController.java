package com.katana.shop.Controller;

import com.katana.shop.dto.CarrelloDto;
import com.katana.shop.dto.CarrelloRequest;
import com.katana.shop.dto.QuantitaRequest;
import com.katana.shop.dto.UnisciCarrelloRequest;
import com.katana.shop.model.Utente;
import com.katana.shop.service.CarrelloService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrello")
public class CarrelloController {
    private final CarrelloService carrelloService;

    public CarrelloController(CarrelloService carrelloService) {
        this.carrelloService = carrelloService;
    }

    @GetMapping
    public CarrelloDto leggi(@AuthenticationPrincipal Utente utente) {
        return carrelloService.leggi(utente.getId());
    }

    @PostMapping("/righe")
    public CarrelloDto aggiungi(@AuthenticationPrincipal Utente utente,
                                @Valid @RequestBody CarrelloRequest richiesta) {
        return carrelloService.aggiungi(utente.getId(), richiesta.katanaId(), richiesta.quantita());
    }

    @PutMapping("/righe/{katanaId}")
    public CarrelloDto impostaQuantita(@AuthenticationPrincipal Utente utente,
                                       @PathVariable Long katanaId,
                                       @Valid @RequestBody QuantitaRequest richiesta) {
        return carrelloService.impostaQuantita(utente.getId(), katanaId, richiesta.quantita());
    }

    @DeleteMapping("/righe/{katanaId}")
    public CarrelloDto rimuovi(@AuthenticationPrincipal Utente utente,
                               @PathVariable Long katanaId) {
        return carrelloService.rimuovi(utente.getId(), katanaId);
    }

    @DeleteMapping
    public CarrelloDto svuota(@AuthenticationPrincipal Utente utente) {
        return carrelloService.svuota(utente.getId());
    }

    @PostMapping("/unisci")
    public CarrelloDto unisci(@AuthenticationPrincipal Utente utente,
                              @Valid @RequestBody UnisciCarrelloRequest richiesta) {
        return carrelloService.unisci(utente.getId(), richiesta.righe());
    }
}