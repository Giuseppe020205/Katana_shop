package com.katana.shop.service;

import com.katana.shop.Repository.KatanaRepository;
import com.katana.shop.dto.KatanaDto;
import com.katana.shop.exception.KatanaNonTrovataException;
import com.katana.shop.model.Katana;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class KatanaService {
    private final KatanaRepository katanaRepository;

    public KatanaService(KatanaRepository katanaRepository) {
        this.katanaRepository = katanaRepository;
    }

    public List<KatanaDto> elencoKatane() {
        return katanaRepository.findAll().stream().map(KatanaDto::from).toList();
    }

    public List<KatanaDto> elencoOfferte() {
        return katanaRepository.findAll().stream()
                .filter(k -> k.inOfferta() && k.getGiacenza_magazzino() > 0)
                .sorted(Comparator.comparingInt(Katana::getPercentualeSconto).reversed())
                .map(KatanaDto::from)
                .toList();
    }

    public KatanaDto trovaPerId(Long id) {
        return katanaRepository.findById(id)
                .map(KatanaDto::from)
                .orElseThrow(() -> new KatanaNonTrovataException(id));
    }


}