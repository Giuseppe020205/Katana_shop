package com.katana.shop.service;

import com.katana.shop.Repository.UtenteRepository;
import com.katana.shop.dto.RegistrazioneRequest;
import com.katana.shop.dto.UtenteDto;
import com.katana.shop.exception.UtenteGiaEsistenteException;
import com.katana.shop.model.Ruolo;
import com.katana.shop.model.Utente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UtenteService {
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    public UtenteService(UtenteRepository utenteRepository,PasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
        this.utenteRepository=utenteRepository;
    }
    @Transactional
    public UtenteDto registra(RegistrazioneRequest request) {
        if (utenteRepository.existsByUsername(request.username())) {
            throw new UtenteGiaEsistenteException("utente già esistente");
        }
        if (utenteRepository.existsByEmail(request.email())) {
            throw new UtenteGiaEsistenteException("utente gia esistente");
        }
        Utente utente=new Utente(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                Ruolo.USER
        );
        return UtenteDto.from(utenteRepository.save(utente));
    }
}
