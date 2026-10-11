package com.katana.shop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UnisciCarrelloRequest(
        @NotNull(message = "L'elenco delle righe è obbligatorio")
        @Size(max = 100, message = "Troppe righe nel carrello")
        List<@Valid CarrelloRequest> righe
) {
}