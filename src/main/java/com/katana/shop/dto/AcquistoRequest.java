package com.katana.shop.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public record AcquistoRequest(
        @NotNull(message = "L'ID della katana è obbligatorio")
        Long idKatana,

        @Min(value = 1, message = "La quantità minima è 1")
        int quantita
) {
}
