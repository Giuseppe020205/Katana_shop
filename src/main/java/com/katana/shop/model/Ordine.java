package com.katana.shop.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordini")
public class Ordine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_ordine", nullable = false)
    private LocalDateTime dataOrdine;

    @Column(name = "totale", nullable = false)
    private BigDecimal totale = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false, length = 30)
    private StatoOrdine stato = StatoOrdine.IN_ATTESA_PAGAMENTO;

    @Column(name = "stripe_session_id", unique = true)
    private String stripeSessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RigaOrdine> righe = new ArrayList<>();

    public Ordine() {
    }

    public Ordine(Utente utente) {
        this.dataOrdine = LocalDateTime.now();
        this.utente = utente;
    }

    public void aggiungiRiga(Katana katana, int quantita) {
        RigaOrdine riga = new RigaOrdine(this, katana, quantita, katana.getPrezzoEffettivo());righe.add(riga);
        totale = totale.add(riga.getSubtotale());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(LocalDateTime dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public BigDecimal getTotale() {
        return totale;
    }

    public void setTotale(BigDecimal totale) {
        this.totale = totale;
    }

    public StatoOrdine getStato() {
        return stato;
    }

    public void setStato(StatoOrdine stato) {
        this.stato = stato;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }

    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public List<RigaOrdine> getRighe() {
        return righe;
    }

    public void setRighe(List<RigaOrdine> righe) {
        this.righe = righe;
    }

}