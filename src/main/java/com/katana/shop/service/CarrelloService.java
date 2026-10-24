package com.katana.shop.service;

import com.katana.shop.Repository.KatanaRepository;
import com.katana.shop.Repository.RigaCarrelloRepository;
import com.katana.shop.Repository.UtenteRepository;
import com.katana.shop.dto.CarrelloDto;
import com.katana.shop.dto.CarrelloRequest;
import com.katana.shop.dto.RigaCarrelloDto;
import com.katana.shop.exception.GiacenzaInsufficienteException;
import com.katana.shop.exception.KatanaNonTrovataException;
import com.katana.shop.model.Katana;
import com.katana.shop.model.RigaCarrello;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarrelloService {
    private final RigaCarrelloRepository rigaCarrelloRepository;
    private final KatanaRepository katanaRepository;
    private final UtenteRepository utenteRepository;

    public CarrelloService(RigaCarrelloRepository rigaCarrelloRepository,KatanaRepository katanaRepository, UtenteRepository utenteRepository){
        this.katanaRepository=katanaRepository;
        this.utenteRepository=utenteRepository;
        this.rigaCarrelloRepository=rigaCarrelloRepository;
    }

    @Transactional(readOnly = true)
    public CarrelloDto leggi(Long utenteId){
        return carrello(utenteId);
    }

    @Transactional
    public CarrelloDto aggiungi(Long utenteId,Long katanaId, int quantita){
        Katana katana=katanaRepository.findById(katanaId).orElseThrow(() -> new KatanaNonTrovataException(katanaId));

        if(katana.getGiacenza_magazzino() <=0){
            throw new GiacenzaInsufficienteException(0,quantita);
        }
        aggiungiRiga(utenteId,katana,quantita);
        return carrello(utenteId);
    }
    @Transactional
    public CarrelloDto rimuovi(Long utenteId,Long katanaId){
        rigaCarrelloRepository.findByUtenteIdAndKatanaId(utenteId, katanaId)
                .ifPresent(rigaCarrelloRepository::delete);
        return carrello(utenteId);
    }
    @Transactional
    public CarrelloDto impostaQuantita(Long utenteId,Long katanaId,int quantita){
        RigaCarrello riga=rigaCarrelloRepository.findByUtenteIdAndKatanaId(utenteId,katanaId)
                .orElseThrow(() -> new KatanaNonTrovataException(katanaId));
        int disponibili=riga.getKatana().getGiacenza_magazzino();
        if(disponibili <=0){
            throw new GiacenzaInsufficienteException(0,quantita);
        }
        riga.setQuantita(Math.min(quantita,disponibili));
        return carrello(utenteId);

    }
    @Transactional
    public CarrelloDto svuota(Long utenteId){
        rigaCarrelloRepository.svuotaCarrello(utenteId);
        return carrello(utenteId);
    }
    //unisce il carrello dell' ospite con quello dell' utente loggato
    @Transactional
    public CarrelloDto unisci(Long utenteId, List<CarrelloRequest> car) {
        for (CarrelloRequest c : car) {
            katanaRepository.findById(c.katanaId())
                    .filter(k -> k.getGiacenza_magazzino() > 0)
                    .ifPresent(k -> aggiungiRiga(utenteId, k, c.quantita()));
        }
        return carrello(utenteId);
    }
    private void aggiungiRiga(Long utenteId, Katana katana, int quantita) {
        int disponibili = katana.getGiacenza_magazzino();
        rigaCarrelloRepository.findByUtenteIdAndKatanaId(utenteId, katana.getId())
                .ifPresentOrElse(
                        riga -> riga.setQuantita(Math.min(riga.getQuantita() + quantita, disponibili)),
                        () -> rigaCarrelloRepository.save(new RigaCarrello(
                                utenteRepository.getReferenceById(utenteId),
                                katana,
                                Math.min(quantita, disponibili))));
    }

    private CarrelloDto carrello(Long utenteId) {
        return CarrelloDto.from(rigaCarrelloRepository.findByUtenteIdOrderById(utenteId));
    }
}
