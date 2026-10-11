package com.katana.shop.Controller;


import com.katana.shop.dto.UtenteAdminDto;
import com.katana.shop.service.UtenteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UtenteService utenteService;

    public AdminController(UtenteService utenteService){
        this.utenteService=utenteService;
    }
    @GetMapping("/utenti")
    public List<UtenteAdminDto> elencoUtenti(){
        return utenteService.elencoUtenti();
    }
}
