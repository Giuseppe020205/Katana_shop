package com.katana.shop.Controller;

import com.katana.shop.dto.RegistrazioneRequest;
import com.katana.shop.dto.UtenteDto;
import com.katana.shop.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UtenteService utenteService;

    public AuthController(UtenteService utenteService){
        this.utenteService=utenteService;
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UtenteDto registra(@Valid @RequestBody RegistrazioneRequest request){
        return utenteService.registra(request);
    }
}
