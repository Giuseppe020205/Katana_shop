package com.katana.shop.Repository;
import com.katana.shop.model.Katana;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface KatanaRepository  extends JpaRepository<Katana,Long> {
    @Query("SELECT k FROM Katana k WHERE k.tipo_acciaio = :tipo_acciaio")
    List<Katana> findByTipo_acciaio(@Param("tipo_acciaio") String tipo_acciaio);

    @Query("SELECT k FROM Katana k WHERE k.tipo_acciaio = :tipoAcciaio")
    List<Katana> findByTipoAcciaio(@Param("tipoAcciaio") String tipoAcciaio);

    Page<Katana> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @Query("SELECT k FROM Katana k WHERE k.prezzo <= :maxPrezzo AND k.giacenza_magazzino > 0 ORDER BY k.prezzo ASC")
    List<Katana> findKataneDisponibiliPerBudget(@Param("maxPrezzo") BigDecimal maxPrezzo);

}

