package com.katana.shop.Repository;

import com.katana.shop.model.RigaCarrello;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RigaCarrelloRepository extends JpaRepository<RigaCarrello, Long> {

    // Tutte le righe del carrello di un utente, già con la katana caricata
    @EntityGraph(attributePaths = "katana")
    List<RigaCarrello> findByUtenteIdOrderById(Long utenteId);

    // La riga di una katana nel carrello di un utente, se esiste
    Optional<RigaCarrello> findByUtenteIdAndKatanaId(Long utenteId, Long katanaId);

    // Svuota il carrello di un utente con una sola istruzione
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RigaCarrello r WHERE r.utente.id = :utenteId")
    void svuotaCarrello(@Param("utenteId") Long utenteId);
}