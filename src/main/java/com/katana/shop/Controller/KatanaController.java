package com.katana.shop.Controller;
import com.katana.shop.dto.KatanaDto;
import com.katana.shop.service.KatanaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/katane")
public class KatanaController {
    private final KatanaService katanaService;

    public KatanaController(KatanaService katanaService) {
        this.katanaService = katanaService;
    }

    @GetMapping
    public List<KatanaDto> getTutteLeKatane() {
        return katanaService.elencoKatane();
    }

    @GetMapping("/offerte")
    public List<KatanaDto> getOfferte() {
        return katanaService.elencoOfferte();
    }

    @GetMapping("/{id}")
    public KatanaDto getKatana(@PathVariable Long id) {
        return katanaService.trovaPerId(id);
    }


}