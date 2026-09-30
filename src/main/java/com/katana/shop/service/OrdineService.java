package com.katana.shop.service;

import com.katana.shop.Repository.KatanaRepository;
import com.katana.shop.model.Katana;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrdineService {
    private final KatanaRepository katanaRepository;

    public OrdineService(KatanaRepository katanaRepository){
        this.katanaRepository=katanaRepository;
    }
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED)
    public void completaAcquisto(String katanaId,int quantita){
        Katana katana=katanaRepository.findById(katanaId).orElseThrow(() -> new IllegalArgumentException("katana non trovata"));

        if (katana.getGiacenza_magazzino() <quantita) {
            throw new IllegalArgumentException(" quantità non disponibile ");
        }
        katana.setGiacenza_magazzino(katana.getGiacenza_magazzino()-quantita);
        katanaRepository.save(katana);
        }
    }
