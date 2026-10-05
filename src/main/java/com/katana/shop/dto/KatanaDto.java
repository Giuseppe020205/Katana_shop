package com.katana.shop.dto;

import com.katana.shop.model.Katana;

import java.math.BigDecimal;

public record KatanaDto(
        Long id,
        String nome,
        String tipo_acciaio,
        BigDecimal prezzo,
        int giacenza
        
) {
    public static KatanaDto from (Katana k) {
        return new KatanaDto(k.getId(), k.getNome(), k.getTipo_acciaio(), k.getPrezzo(), k.getGiacenza_magazzino());
    }
}
