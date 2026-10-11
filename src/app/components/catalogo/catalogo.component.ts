import { Component, OnInit, computed, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Katana } from '../../models/katana.model';
import { CarrelloService } from '../../services/carrello.service';
import { BackButtonComponent } from '../shared/back-button.component';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [DecimalPipe, BackButtonComponent],
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
  notifica = signal<string | null>(null);

  tipiAcciaio = computed(() =>
    [...new Set(this.katane().map(k => k.tipo_acciaio).filter(t => !!t))].sort()
  );

  katanaFiltrate = computed(() => {
    const testo = this.ricerca().trim().toLowerCase();
    const tipo = this.tipoSelezionato();

    const risultato = this.katane().filter(k =>
      (!testo || `${k.nome} ${k.tipo_acciaio ?? ''}`.toLowerCase().includes(testo)) &&
      (!tipo || k.tipo_acciaio === tipo)
    );

    const ord = this.ordinamento();
    if (ord === 'prezzo-asc') risultato.sort((a, b) => a.prezzo - b.prezzo);
    if (ord === 'prezzo-desc') risultato.sort((a, b) => b.prezzo - a.prezzo);
    return risultato;
  });

  private apiUrl = 'http://localhost:8080/api/katane';
  private timerNotifica: ReturnType<typeof setTimeout> | undefined;

  constructor(private http: HttpClient, private carrello: CarrelloService) {}

  ngOnInit(): void {
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

  valore(event: Event): string {
    return (event.target as HTMLInputElement | HTMLSelectElement).value;
  }

  azzeraFiltri(): void {
    this.ricerca.set('');
    this.tipoSelezionato.set('');
    this.ordinamento.set('nessuno');
  }

  inCarrello(k: Katana): number {
    return this.carrello.righe().find(r => r.id === k.id)?.quantita ?? 0;
  }

  async aggiungiAlCarrello(k: Katana): Promise<void> {
      const esito = await this.carrello.aggiungi(k);
      this.mostraNotifica(esito.messaggio);
    }

  private mostraNotifica(testo: string): void {
    this.notifica.set(testo);
    clearTimeout(this.timerNotifica);
    this.timerNotifica = setTimeout(() => this.notifica.set(null), 2500);
  }
}
