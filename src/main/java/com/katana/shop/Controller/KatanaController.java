package com.katana.shop.Controller;

import com.katana.shop.Repository.KatanaRepository;
import com.katana.shop.dto.AcquistoRequest;
import com.katana.shop.dto.KatanaDto;
import com.katana.shop.dto.MessaggioResponse;
import com.katana.shop.model.Katana;
import com.katana.shop.service.KatanaService;
import com.katana.shop.service.OrdineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController

@RequestMapping("/api/katane")
public class KatanaController {
   private final KatanaService katanaService;
    private final OrdineService ordineService;

    public KatanaController(KatanaService katanaService,OrdineService ordineService){
        this.katanaService=katanaService;
        this.ordineService=ordineService;
    }
    @GetMapping
    public List<KatanaDto> getTutteLeKatane(){
        return katanaService.elencoKatane();
    }
    @GetMapping("/{id}")
    public KatanaDto getKatana(@PathVariable Long id){
        return katanaService.trovaPerId(id);
    }
    @PostMapping("/acquista")
    public MessaggioResponse acquistaKatana(@Valid @RequestBody AcquistoRequest request){
        ordineService.completaAcquisto(request.idKatana(),request.quantita());
        return new MessaggioResponse("acquisto effettuato con successo");
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KatanaDto createKatana(@Valid @RequestBody KatanaDto katanaDto){
        return katanaService.creaKatana(katanaDto);

    }


}

