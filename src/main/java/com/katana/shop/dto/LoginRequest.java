package com.katana.shop.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank( message = "lo username è obbligatorio") String username,
        @NotBlank(message = " la password è obbligatoria") String password
){}
