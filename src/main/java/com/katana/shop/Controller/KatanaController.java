package com.katana.shop.Controller;

import com.katana.shop.Repository.KatanaRepository;
import com.katana.shop.service.OrdineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/katane")
public class KatanaController {
    private final KatanaRepository katanaRepository;
    private final OrdineService ordineService;

    public KatanaController(KatanaRepository katanaRepository,OrdineService ordineService){
        this.katanaRepository=katanaRepository;
        this.ordineService=ordineService;
    }

    @GetMapping
    public String catalogo(Model model){
        model.addAttribute("elencoKatane",katanaRepository.findAll());
        return "catalogo";
    }
    @PostMapping("/acquista")
    public String acquistaKatana(@RequestParam("id") String id,@RequestParam("quantita") int quantita){
        ordineService.completaAcquisto(id,quantita);
        return "redirect:/katane?successo=true";

    }
}
