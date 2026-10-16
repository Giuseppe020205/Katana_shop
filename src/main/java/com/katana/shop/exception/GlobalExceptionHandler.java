package com.katana.shop.exception;
import org.springframework.security.core.AuthenticationException;
import com.katana.shop.dto.MessaggioResponse;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UtenteGiaEsistenteException.class)
    public ResponseEntity<MessaggioResponse> utenteGiaEsistente(UtenteGiaEsistenteException e) {
        return risposta(HttpStatus.CONFLICT, e.getMessage());
    }
    @ExceptionHandler(KatanaNonTrovataException.class)
    public ResponseEntity<MessaggioResponse> katanaNonTrovata(KatanaNonTrovataException e) {
        return risposta(HttpStatus.NOT_FOUND, e.getMessage());
    }
    @ExceptionHandler(GiacenzaInsufficienteException.class)
    public ResponseEntity<MessaggioResponse> giacenzaInsufficiente(GiacenzaInsufficienteException e) {
        return risposta(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<MessaggioResponse> conflittoDiConcorrenza(OptimisticLockingFailureException e) {
        return risposta(HttpStatus.CONFLICT, "La katana è stata modificata da un altro acquisto, riprova.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessaggioResponse> validazione(MethodArgumentNotValidException e) {
        String messaggio = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Richiesta non valida");
        return risposta(HttpStatus.BAD_REQUEST, messaggio);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessaggioResponse> corpoNonLeggibile(HttpMessageNotReadableException e) {
        return risposta(HttpStatus.BAD_REQUEST, "Corpo della richiesta non valido");
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<MessaggioResponse> nonAutenticato(AuthenticationException e){
        return risposta(HttpStatus.UNAUTHORIZED, "username e password errati");
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessaggioResponse> conflittoDati(DataIntegrityViolationException e) {
        return risposta(HttpStatus.CONFLICT, "Operazione non riuscita per un conflitto con altri dati, riprova.");
    }
    @ExceptionHandler(CarrelloNonAcquistabileException.class)
    public ResponseEntity<MessaggioResponse> carrelloNonAcquistabile(CarrelloNonAcquistabileException e) {
        return risposta(HttpStatus.CONFLICT, e.getMessage());
    }
    @ExceptionHandler(PagamentoNonDisponibileException.class)
    public ResponseEntity<MessaggioResponse> pagamentoNonDisponibile(PagamentoNonDisponibileException e) {
        return risposta(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    private ResponseEntity<MessaggioResponse> risposta(HttpStatus status, String messaggio) {
        return ResponseEntity.status(status).body(new MessaggioResponse(messaggio));
    }

}

