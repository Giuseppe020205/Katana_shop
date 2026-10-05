package com.katana.shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrazioneRequest(
    @NotBlank(message = "lo username è obbligatorio")
    @Size(min=3,max = 50)
    String username,

    @NotBlank(message = "l' email è obbligatoria")
    @Email(message = "email non valida")
    @Size(max=100)
    String email,

    @NotBlank(message = "la password è obbligatoria")
    @Size(min=8,max=100)
    String password
){

}
