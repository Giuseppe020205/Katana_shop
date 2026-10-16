package com.katana.shop.dto;

import com.katana.shop.model.Katana;
import com.katana.shop.model.RigaCarrello;

import java.math.BigDecimal;

public record RigaCarrelloDto(
        Long katanaId,
        String nome,
        String immagine,
        BigDecimal prezzo,
        BigDecimal prezzoOriginale,
        Integer percentualeSconto,
        int giacenza_magazzino,
        int quantita,
        BigDecimal subtotale,
        boolean disponibile,
        String avviso
) {
    public static RigaCarrelloDto from(RigaCarrello riga) {
        Katana k = riga.getKatana();
        int giacenza = Math.max(k.getGiacenza_magazzino(), 0);
        boolean disponibile = giacenza > 0;
        int quantita = riga.getQuantita();
        String avviso = null;

        if (!disponibile) {
            avviso = "Non più disponibile";
        } else if (quantita > giacenza) {
            quantita = giacenza;
            avviso = "Quantità ridotta: " + (giacenza == 1 ? "è rimasto solo 1 pezzo" : "sono rimasti solo " + giacenza + " pezzi");
        }


        boolean inOfferta = k.inOfferta();
        BigDecimal prezzo = inOfferta ? k.getPrezzoEffettivo() : k.getPrezzo();
        Integer percentuale = inOfferta ? k.getPercentualeSconto() : null;

        BigDecimal subtotale = disponibile ? prezzo.multiply(BigDecimal.valueOf(quantita)) : BigDecimal.ZERO;

        return new RigaCarrelloDto(k.getId(), k.getNome(), k.getImmagine(), prezzo, k.getPrezzo(), percentuale,
                giacenza, quantita, subtotale, disponibile, avviso);
    }
}