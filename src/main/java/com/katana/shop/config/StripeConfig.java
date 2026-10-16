package com.katana.shop.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    private static final Logger log = LoggerFactory.getLogger(StripeConfig.class);

    private final String chiaveSegreta;

    public StripeConfig(@Value("${app.stripe.secret-key}") String chiaveSegreta) {
        this.chiaveSegreta = chiaveSegreta;
    }

    @PostConstruct
    void configura() {
        if (chiaveSegreta == null || chiaveSegreta.isBlank()) {
            log.warn("Stripe: chiave segreta non impostata (variabile STRIPE_SECRET_KEY). I pagamenti non funzioneranno.");
            return;
        }
        Stripe.apiKey = chiaveSegreta;
        if (chiaveSegreta.startsWith("sk_test_")) {
            log.info("Stripe configurato in modalità TEST");
        } else {
            log.warn("Stripe: la chiave non inizia con sk_test_. Per lo sviluppo usa solo chiavi di prova.");
        }
    }
}