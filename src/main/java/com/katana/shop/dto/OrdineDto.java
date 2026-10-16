package com.katana.shop.dto;

import com.katana.shop.model.Ordine;
import com.katana.shop.model.StatoOrdine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdineDto (
        Long id,
        StatoOrdine stato,
        LocalDateTime dataOrdine,
        BigDecimal totale,
        List<RigaOrdineDto> righe
){
    public static  OrdineDto from(Ordine ordine){
        return new OrdineDto(
                ordine.getId(),
                ordine.getStato(),
                ordine.getDataOrdine(),
                ordine.getTotale(),
                ordine.getRighe().stream().map(RigaOrdineDto :: from).toList()
        );
    }

}
