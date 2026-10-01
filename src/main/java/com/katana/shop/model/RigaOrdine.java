package com.katana.shop.model;


import jakarta.persistence.*;

import java.awt.image.renderable.RenderedImageFactory;
import java.math.BigDecimal;

@Entity
@Table(name="righe_ordine")
public class RigaOrdine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="ordine_id",nullable = false)
    private Ordine ordine;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="katana_id",nullable = false)
    private Katana katana;

    @Column(name="quantita",nullable = false)
    private int quantita;

    @Column(name="prezzo_unitario",nullable = false,precision = 10,scale = 2)
    private BigDecimal prezzoUnitario;

    public RigaOrdine(){

    }
    public RigaOrdine(Ordine ordine,Katana katana,int quantita,BigDecimal prezzoUnitario){
        this.ordine=ordine;
        this.katana=katana;
        this.quantita=quantita;
        this.prezzoUnitario=prezzoUnitario;
    }
    public BigDecimal getSubtotale(){return prezzoUnitario.multiply(BigDecimal.valueOf(quantita));}

    public void setQuantita(int quantita) {this.quantita = quantita;}

    public int getQuantita() {return quantita;}

    public void setId(Long id) {this.id = id;}

    public Long getId() {return id;}

    public Ordine getOrdine() {return ordine;}

    public void setOrdine(Ordine ordine) {this.ordine = ordine;}

    public BigDecimal getPrezzoUnitario() {return prezzoUnitario;}

    public void setPrezzoUnitario(BigDecimal prezzoUnitario) {this.prezzoUnitario = prezzoUnitario;}

    public Katana getKatana() {return katana;}
    public void setKatana(Katana katana) {this.katana = katana;}

}

