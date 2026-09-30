package com.katana.shop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="ordini")
public class Ordine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name="data_ordine" ,nullable = false)
    private LocalDateTime dataOrdine;

    @Column(name="totale",nullable = false)
    private BigDecimal totale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="utente_id",nullable = false)
    private Utente utente;

    @Column(name="quantita", nullable = false)
    private int quantita;

    public Ordine(){
        this.dataOrdine=LocalDateTime.now();
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getTotale() {
        return totale;
    }

    public int getQuantita() {
        return quantita;
    }

    public LocalDateTime getDataOrdine() {
        return dataOrdine;
    }

    public String getId() {
        return id;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setTotale(BigDecimal totale) {
        this.totale = totale;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

}
