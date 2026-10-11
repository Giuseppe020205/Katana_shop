package com.katana.shop.Controller;

import com.katana.shop.dto.AuthResponse;
import com.katana.shop.dto.LoginRequest;
import com.katana.shop.dto.RegistrazioneRequest;
import com.katana.shop.dto.UtenteDto;
import com.katana.shop.model.Utente;
import com.katana.shop.service.AuthService;
import com.katana.shop.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UtenteService utenteService;
    private final AuthService authService;

    public AuthController(UtenteService utenteService,AuthService authService){
        this.utenteService=utenteService;
        this.authService=authService;
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UtenteDto registra(@Valid @RequestBody RegistrazioneRequest request){
        return utenteService.registra(request);
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
    @GetMapping("/me")
    public UtenteDto me(@AuthenticationPrincipal Utente utente){
        return UtenteDto.from(utente);
    }
}
