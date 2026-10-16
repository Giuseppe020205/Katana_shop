package com.katana.shop.dto;

import com.katana.shop.model.Katana;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record KatanaDto(
        Long id,
        String nome,
        String tipo_acciaio,
        BigDecimal prezzo,
        int giacenza_magazzino,
        @Pattern(regexp = "^/img/katane/[a-z0-9][a-z0-9._-]*\\.(jpg|jpeg|png|webp)$",
                message = "Percorso immagine non valido: usa /img/katane/nome-file.jpg (minuscolo, senza spazi)")
        String immagine,
        boolean inOfferta,
        Integer percentualeSconto,
        BigDecimal prezzoScontato,
        LocalDateTime offertaFine
) {
    public static KatanaDto from(Katana k) {
        boolean inOfferta = k.inOfferta();
        return new KatanaDto(
                k.getId(),
                k.getNome(),
                k.getTipo_acciaio(),
                k.getPrezzo(),
                k.getGiacenza_magazzino(),
                k.getImmagine(),
                inOfferta,
                inOfferta ? k.getPercentualeSconto() : null,
                inOfferta ? k.getPrezzoEffettivo() : null,
                inOfferta ? k.getOffertaFine() : null);
    }
}