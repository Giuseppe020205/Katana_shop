import { Component, OnInit, computed, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Katana } from '../../models/katana.model';
import { CarrelloService } from '../../services/carrello.service';
import { BackButtonComponent } from '../shared/back-button.component';
import { PrezzoKatanaComponent } from '../shared/prezzo-katana.component';
import { KatanaImmagineComponent } from '../shared/katana-immagine.component';

interface FasciaPrezzo {
  id: string;
  etichetta: string;
  min: number;        // incluso
  max: number | null; // escluso; null = nessun limite
}

const FASCE_PREZZO: FasciaPrezzo[] = [
  { id: '200-1000', etichetta: '€ 200 – 1000', min: 200, max: 1000 },
  { id: '1000-2500', etichetta: '€ 1000 – 2500', min: 1000, max:2500  },
  { id: '2500+', etichetta: '€ 2500 e oltre', min: 2500, max: null },

];

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [DatePipe, BackButtonComponent, PrezzoKatanaComponent, KatanaImmagineComponent],
  templateUrl: './catalogo.component.html',
  styleUrls: ['./catalogo.component.css']
})
export class CatalogoComponent implements OnInit {
  katane = signal<Katana[]>([]);
  caricamento = signal(true);
  errore = signal<string | null>(null);

  ricerca = signal('');
  tipoSelezionato = signal('');
  ordinamento = signal('nessuno');
  soloOfferte = signal(false);
  notifica = signal<string | null>(null);

  fasce = FASCE_PREZZO;

  tipiAcciaio = computed(() =>
    [...new Set(this.katane().map(k => k.tipo_acciaio).filter(t => !!t))].sort()
  );

  katanaFiltrate = computed(() => {
    const testo = this.ricerca().trim().toLowerCase();
    const tipo = this.tipoSelezionato();
    const solo = this.soloOfferte();
    const scelta = this.ordinamento();
    const fascia = this.fasce.find(f => scelta === 'fascia-' + f.id);

    const risultato = this.katane().filter(k =>
      (!testo || `${k.nome} ${k.tipo_acciaio ?? ''}`.toLowerCase().includes(testo)) &&
      (!tipo || k.tipo_acciaio === tipo) &&
      (!solo || k.inOfferta) &&
      (!fascia || this.nellaFascia(k, fascia))
    );

    if (scelta === 'prezzo-asc') risultato.sort((a, b) => this.prezzoFinale(a) - this.prezzoFinale(b));
    if (scelta === 'prezzo-desc') risultato.sort((a, b) => this.prezzoFinale(b) - this.prezzoFinale(a));
    return risultato;
  });

  private apiUrl = 'http://localhost:8080/api/katane';
  private timerNotifica: ReturnType<typeof setTimeout> | undefined;

  constructor(
    private http: HttpClient,
    private carrello: CarrelloService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.soloOfferte.set(this.route.snapshot.queryParamMap.get('offerte') === '1');
    this.caricaKatane();
  }

  caricaKatane(): void {
    this.http.get<Katana[]>(this.apiUrl).subscribe({
      next: (data) => {
        this.katane.set(data);
        this.caricamento.set(false);
      },
      error: (err) => {
        this.errore.set('Impossibile caricare il catalogo katane.');
        this.caricamento.set(false);
        console.error(err);
      }
    });
  }

  prezzoFinale(k: Katana): number {
    return k.inOfferta && k.prezzoScontato != null ? k.prezzoScontato : k.prezzo;
  }

  private nellaFascia(k: Katana, fascia: FasciaPrezzo): boolean {
    const prezzo = this.prezzoFinale(k);
    return prezzo >= fascia.min && (fascia.max === null || prezzo < fascia.max);
  }

  valore(event: Event): string {
    return (event.target as HTMLInputElement | HTMLSelectElement).value;
  }

  selezionato(event: Event): boolean {
    return (event.target as HTMLInputElement).checked;
  }

  azzeraFiltri(): void {
    this.ricerca.set('');
    this.tipoSelezionato.set('');
    this.ordinamento.set('nessuno');
    this.soloOfferte.set(false);
  }

  inCarrello(k: Katana): number {
    return this.carrello.righe().find(r => r.id === k.id)?.quantita ?? 0;
  }

  selezioni = signal<Record<number, number>>({});

  quantitaScelta(k: Katana): number {
    return k.id != null ? this.selezioni()[k.id] ?? 0 : 0;
  }

  disponibiliDaAggiungere(k: Katana): number {
    return k.giacenza_magazzino - this.inCarrello(k);
  }

  apriSelettore(k: Katana): void {
    this.impostaScelta(k, 1);
  }

  chiudiSelettore(k: Katana): void {
    this.impostaScelta(k, 0);
  }

  aumenta(k: Katana): void {
    const q = this.quantitaScelta(k);
    if (q < this.disponibiliDaAggiungere(k)) this.impostaScelta(k, q + 1);
  }

  diminuisci(k: Katana): void {
    const q = this.quantitaScelta(k);
    if (q > 1) this.impostaScelta(k, q - 1);
  }

  async conferma(k: Katana): Promise<void> {
    const quantita = this.quantitaScelta(k);
    if (quantita <= 0) return;
    const esito = await this.carrello.aggiungi(k, quantita);
    this.mostraNotifica(
      esito.aggiunta && quantita > 1 ? `${quantita} × "${k.nome}" aggiunte al carrello` : esito.messaggio
    );
    if (esito.aggiunta) this.chiudiSelettore(k);
  }

  private impostaScelta(k: Katana, quantita: number): void {
    if (k.id == null) return;
    this.selezioni.update(s => ({ ...s, [k.id!]: quantita }));
  }

  private mostraNotifica(testo: string): void {
    this.notifica.set(testo);
    clearTimeout(this.timerNotifica);
    this.timerNotifica = setTimeout(() => this.notifica.set(null), 2500);
  }
}

