package com.katana.shop.Repository;
import com.katana.shop.model.Ordine;
import com.katana.shop.model.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrdineRepository  extends JpaRepository<Ordine,Long> {

    Optional<Ordine> findByStripeSessionId(String stripeSessionId);

    List<Ordine> findByUtenteIdAndStato(Long utenteId, StatoOrdine stato);

    List<Ordine> findByUtenteIdAndStatoInOrderByDataOrdineDescIdDesc(Long utenteId, Collection<StatoOrdine> stati);


}
