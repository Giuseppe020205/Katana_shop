package com.katana.shop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public  record CarrelloRequest(
    @NotNull(message = "L' ID della katana è obbligatorio")
    Long katanaId,
    @Min(value = 1,message = "la quantità minima è 1")
    @Max(value = 999, message = "la quantità massima è 999")
    int quantita
            ){

}

