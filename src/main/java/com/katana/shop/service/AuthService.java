package com.katana.shop.service;

import com.katana.shop.Repository.UtenteRepository;
import com.katana.shop.dto.AuthResponse;
import com.katana.shop.dto.LoginRequest;
import com.katana.shop.dto.UtenteDto;
import com.katana.shop.model.Utente;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UtenteRepository utenteRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,UtenteRepository utenteRepository,JwtService jwtService){
        this.authenticationManager=authenticationManager;
        this.utenteRepository=utenteRepository;
        this.jwtService=jwtService;
    }

    public AuthResponse login (LoginRequest richiesta){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(richiesta.username(),richiesta.password())
        );
        Utente utente=utenteRepository.findByUsername(richiesta.username()).orElseThrow();
        return new AuthResponse(jwtService.genera(utente),UtenteDto.from(utente));
    }
}
