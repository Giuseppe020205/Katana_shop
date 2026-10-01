package com.katana.shop.Repository;

import com.katana.shop.model.Katana;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface KatanaRepository  extends JpaRepository<Katana,String> {
    List<Katana> findByTipoAcciaio(String tipoAcciaio );

    Page<Katana> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    // Query JPQL personalizzata
    @Query("SELECT k FROM Katana k WHERE k.prezzo <= :maxPrezzo AND k.stock > 0 ORDER BY k.prezzo ASC")
    List<Katana> findKataneDisponibiliPerBudget(@Param("maxPrezzo") BigDecimal maxPrezzo);
}

