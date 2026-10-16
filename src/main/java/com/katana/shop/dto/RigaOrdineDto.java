package com.katana.shop.dto;

import com.katana.shop.model.RigaOrdine;

import java.math.BigDecimal;

public record RigaOrdineDto (
        Long katanaId,
        String nome,
        int quantita,
        BigDecimal prezzoUnitario,
        BigDecimal subtotale
){
    public static  RigaOrdineDto from(RigaOrdine riga){
        return new RigaOrdineDto(
                riga.getKatana().getId(),
                riga.getKatana().getNome(),
                riga.getQuantita(),
                riga.getPrezzoUnitario(),
                riga.getSubtotale()
        );
    }
}
