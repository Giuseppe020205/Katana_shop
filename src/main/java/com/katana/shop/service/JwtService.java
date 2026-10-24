package com.katana.shop.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class  JwtService {
    private final SecretKey chiave;
    private final long scadenzaMs;

    public JwtService(@Value("${app.jwt.secret}") String segreto,
                      @Value("${app.jwt.expiration-ms}") long scadenzaMs) {
        this.chiave = Keys.hmacShaKeyFor(segreto.getBytes(StandardCharsets.UTF_8));
        this.scadenzaMs = scadenzaMs;
    }

        public String genera(UserDetails utente) {
            Date ora = new Date();
            return Jwts.builder()
                    .subject(utente.getUsername())
                    .issuedAt(ora)
                    .expiration(new Date(ora.getTime() + scadenzaMs))
                    .signWith(chiave)
                    .compact();
        }

    public String estraiUsername(String token) {
        return claims(token).getSubject();
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith(chiave).build().parseSignedClaims(token).getPayload();
    }

}