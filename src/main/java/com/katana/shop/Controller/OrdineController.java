package com.katana.shop.Controller;

import com.katana.shop.dto.OrdineDto;
import com.katana.shop.model.Utente;
import com.katana.shop.service.OrdineService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/ordini")
public class OrdineController {
    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }

    // Storico degli ordini completati o annullati dell'utente loggato
    @GetMapping
    public List<OrdineDto> mieiOrdini(@AuthenticationPrincipal Utente utente) {
        return ordineService.storico(utente.getId());
    }
}