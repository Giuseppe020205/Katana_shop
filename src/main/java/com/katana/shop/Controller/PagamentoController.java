package com.katana.shop.Controller;


import com.katana.shop.dto.CheckoutResponse;
import com.katana.shop.model.Utente;
import com.katana.shop.service.PagamentoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagamenti")
public class PagamentoController {
    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService){
        this.pagamentoService=pagamentoService;
    }
    @PostMapping("/checkout")
    public CheckoutResponse avviaCheckout(@AuthenticationPrincipal Utente utente) {
        return pagamentoService.avviaCheckout(utente.getId(), utente.getEmail());
    }
}

