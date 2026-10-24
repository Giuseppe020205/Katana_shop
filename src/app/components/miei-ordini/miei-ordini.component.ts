import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrdiniService, OrdineStorico, StatoOrdine } from '../../services/ordini.service';
import { messaggioErrore } from '../../utils/errori';
import { BackButtonComponent } from '../shared/back-button.component';
import { KatanaImmagineComponent } from '../shared/katana-immagine.component';

const ETICHETTE_STATO: Record<StatoOrdine, string> = {
  IN_ATTESA_PAGAMENTO: 'In attesa di pagamento',
  PAGATO: 'Completato',
  ANNULLATO: 'Annullato'
};

@Component({
  selector: 'app-miei-ordini',
  standalone: true,
  imports: [DatePipe, DecimalPipe, RouterLink, BackButtonComponent, KatanaImmagineComponent],
  templateUrl: './miei-ordini.component.html',
  styleUrls: ['./miei-ordini.component.css']
})
export class MieiOrdiniComponent implements OnInit {
  private ordiniService = inject(OrdiniService);

  ordini = signal<OrdineStorico[]>([]);
  caricamento = signal(true);
  errore = signal<string | null>(null);

  ngOnInit(): void {
    this.ordiniService.mieiOrdini().subscribe({
      next: (lista) => {
        this.ordini.set(lista);
        this.caricamento.set(false);
      },
      error: (err) => {
        this.errore.set(messaggioErrore(err, 'Impossibile caricare i tuoi ordini.'));
        this.caricamento.set(false);
      }
    });
  }

  etichettaStato(stato: StatoOrdine): string {
    return ETICHETTE_STATO[stato] ?? stato;
  }
}
