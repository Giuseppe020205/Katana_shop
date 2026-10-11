package com.katana.shop.dto;

import com.katana.shop.model.Utente;

public record UtenteAdminDto(Long id, String username, String email, String ruolo){
    public static UtenteAdminDto from (Utente u){
        return new UtenteAdminDto(u.getId(), u.getUsername(), u.getEmail(), u.getRuolo().name());
    }
}
