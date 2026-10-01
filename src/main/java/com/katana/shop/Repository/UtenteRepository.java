package com.katana.shop.Repository;

import com.katana.shop.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente,Long> {
    Optional<Utente> findByUsername(String username);
    boolean existByUsername(String username);
    boolean existByEmail(String email);
}
