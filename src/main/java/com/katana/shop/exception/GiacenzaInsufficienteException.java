package com.katana.shop.exception;

public class GiacenzaInsufficienteException extends RuntimeException {
    public GiacenzaInsufficienteException(int disponibili, int richieste) {
        super("Quantità non disponibile: richiesti " + richieste + ", disponibili " + disponibili);
    }
}