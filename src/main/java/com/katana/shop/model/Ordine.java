package com.katana.shop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="ordini")
public class Ordine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="data_ordine" ,nullable = false)
    private LocalDateTime dataOrdine;

    @Column(name="totale",nullable = false)
    private BigDecimal totale;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="utente_id",nullable = false)
    private Utente utente;

   @OneToMany(mappedBy = "ordine",cascade = CascadeType.ALL,orphanRemoval = true)
   private List<RigaOrdine> righe=new ArrayList<>();

    public Ordine(){

    }
    public Ordine(Utente utente){
        this.dataOrdine=LocalDateTime.now();
        this.utente=utente;
    }
    public void aggiungiRiga(Katana katana,int quantita){
        RigaOrdine riga=new RigaOrdine(this,katana,quantita,katana.getPrezzo());
        riga.add(riga);
        totale=totale.add(riga.getSubtotale());
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotale() {
        return totale;
    }


    public LocalDateTime getDataOrdine() {
        return dataOrdine;
    }

    public Long getId() {
        return id;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setTotale(BigDecimal totale) {
        this.totale = totale;
    }


    public void setUtente(Utente utente) {
        this.utente = utente;
    }

}
