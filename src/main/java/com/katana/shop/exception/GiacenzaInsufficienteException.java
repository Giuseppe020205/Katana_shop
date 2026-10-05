package com.katana.shop.exception;

public class GiacenzaInsufficienteException extends RuntimeException{
    public GiacenzaInsufficienteException(int disponibili, int richieste){
        super("quantità non disponiile: rihciesti"+richieste+ ", disponibili"+disponibili);
    }
}
