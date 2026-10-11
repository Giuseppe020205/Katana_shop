package com.katana.shop.dto;

import com.katana.shop.model.Katana;
import com.katana.shop.model.RigaCarrello;

import java.math.BigDecimal;

public record RigaCarrelloDto(
        Long katanaId,
        String nome,
        BigDecimal prezzo,
        int giacenza_magazzino,
        int quantita,
        BigDecimal subtotale,
        boolean disponibile,
        String avviso
) {
    public static  RigaCarrelloDto from(RigaCarrello riga){
        Katana k=riga.getKatana();
        int giacenza=Math.max(k.getGiacenza_magazzino(),0);
        boolean disponibile=giacenza>0;
        int quantita=riga.getQuantita();
        String avviso=null;

        if(!disponibile){
            avviso="Non più disponibile";
        }else if(quantita>giacenza){
            quantita=giacenza;
            avviso = "Quantità ridotta: " + (giacenza == 1 ? "è rimasto solo 1 pezzo" : "sono rimasti solo " + giacenza + " pezzi");

        }
        BigDecimal subtotale=disponibile ? k.getPrezzo().multiply(BigDecimal.valueOf(quantita)):BigDecimal.ZERO;
        return new RigaCarrelloDto(k.getId(),k.getNome(),k.getPrezzo(),giacenza,quantita,subtotale,disponibile,avviso);
    }
}
