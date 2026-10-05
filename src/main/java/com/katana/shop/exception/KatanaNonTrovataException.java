package com.katana.shop.exception;

public class KatanaNonTrovataException extends RuntimeException {
    public KatanaNonTrovataException(Long id) {
        super("Katana con id " + id + " non trovata");
    }
}
