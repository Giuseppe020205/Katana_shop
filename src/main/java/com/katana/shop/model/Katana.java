package com.katana.shop.model;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Katane")
public class Katana {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name= "nome",nullable = false,length = 100)
    private String nome;

    @Column (name= "tipo_acciaio", length = 50)
    private String tipo_acciaio;

    @Column (name = "prezzo",nullable = false)
    private BigDecimal prezzo;

    @Column (name = "giacenza_magazzino")
    private int giacenza_magazzino;

    @Version
    private Long version;

    public Katana(){}

    public Katana(String nome,String tipo_acciaio,BigDecimal prezzo,int giacenza_magazzino){
        this.nome=nome;
        this.tipo_acciaio=tipo_acciaio;
        this.giacenza_magazzino=giacenza_magazzino;
        this.prezzo=prezzo;
    }
    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public String getId() {
        return id;
    }

    public int getGiacenza_magazzino() {
        return giacenza_magazzino;
    }

    public Long getVersion() {
        return version;
    }

    public String getTipo_acciaio() {
        return tipo_acciaio;
    }

    public void setGiacenza_magazzino(int giacenza_magazzino) {
        this.giacenza_magazzino = giacenza_magazzino;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTipo_acciaio(String tipo_acciaio) {
        this.tipo_acciaio = tipo_acciaio;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}

