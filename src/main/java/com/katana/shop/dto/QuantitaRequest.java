package com.katana.shop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record QuantitaRequest(
        @Min(value = 1, message = "La quantità minima è 1")
        @Max(value = 999, message = "La quantità massima è 999")
        int quantita
) {
}