package com.katana.shop.service;
import com.katana.shop.Repository.UtenteRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtenteDetailsService implements UserDetailsService {

    private final UtenteRepository utenteRepository;

    public UtenteDetailsService (UtenteRepository utenteRepository){
        this.utenteRepository=utenteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return utenteRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("utente non trovato") );
    }

}
