package com.katana.shop.dto;

import com.katana.shop.model.RigaCarrello;

import java.math.BigDecimal;
import java.util.List;

public record CarrelloDto(
        List<RigaCarrelloDto> righe,
        int numeroArticoli,
        BigDecimal totale,
        boolean acquistabile
) {
    public static CarrelloDto from(List<RigaCarrello> righe) {
        List<RigaCarrelloDto> dto = righe.stream().map(RigaCarrelloDto::from).toList();

        int numeroArticoli = dto.stream().filter(RigaCarrelloDto::disponibile).mapToInt(RigaCarrelloDto::quantita).sum();

        BigDecimal totale = dto.stream().map(RigaCarrelloDto::subtotale).reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean acquistabile = !dto.isEmpty() && dto.stream().allMatch(RigaCarrelloDto::disponibile);

        return new CarrelloDto(dto, numeroArticoli, totale, acquistabile);
    }
}
