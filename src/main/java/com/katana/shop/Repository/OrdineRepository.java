package com.katana.shop.Repository;

import com.katana.shop.model.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdineRepository  extends JpaRepository<Ordine,Long> {
    List<Ordine > findByUtenteOrderByDataOrdine(Long utenteId);

}
