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


    @EntityGraph(attributePaths = "katana")
    List<RigaCarrello> findByUtenteIdOrderById(Long utenteId);

    Optional<RigaCarrello> findByUtenteIdAndKatanaId(Long utenteId, Long katanaId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RigaCarrello r WHERE r.utente.id = :utenteId")
    void svuotaCarrello(@Param("utenteId") Long utenteId);
}