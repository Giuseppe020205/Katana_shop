package com.katana.shop.exception;

public class CarrelloNonAcquistabileException extends RuntimeException {
    public CarrelloNonAcquistabileException(String messaggio) {
        super(messaggio);
    }
}