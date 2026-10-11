package com.katana.shop.model;


import com.katana.shop.service.KatanaService;
import com.katana.shop.service.UtenteService;
import jakarta.persistence.*;

@Entity
@Table(name= "righe_carrello",uniqueConstraints = @UniqueConstraint(columnNames ={"utente_id","katana_id"} ))

public class RigaCarrello {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name= "utente_id",nullable = false)
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="katana_id",nullable = false)
    private Katana katana;

    @Column(name="quantita",nullable = false)
    private int quantita;

    public RigaCarrello(){

    }
    public RigaCarrello(Utente utente,Katana katana,int quantita){
        this.utente=utente;
        this.katana=katana;
        this.quantita=quantita;

    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Katana getKatana() {
        return katana;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setKatana(Katana katana) {
        this.katana = katana;
    }

}
