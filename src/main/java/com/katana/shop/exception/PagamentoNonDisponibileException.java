package com.katana.shop.exception;

public class PagamentoNonDisponibileException extends RuntimeException {
    public PagamentoNonDisponibileException(String messaggio) {
        super(messaggio);
    }
}