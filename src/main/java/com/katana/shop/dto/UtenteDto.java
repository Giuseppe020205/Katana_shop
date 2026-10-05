package com.katana.shop.dto;

import com.katana.shop.model.Utente;

public record UtenteDto(
        Long id,
        String username,
        String email
) {
    public static UtenteDto from(Utente u){
        return new UtenteDto(u.getId(),u.getUsername(),u.getEmail());
    }

}
