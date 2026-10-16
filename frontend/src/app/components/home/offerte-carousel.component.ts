import { Component, DestroyRef, ElementRef, OnInit, PLATFORM_ID, inject, signal, viewChild } from '@angular/core';
import { DatePipe, isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { Katana } from '../../models/katana.model';
import { CarrelloService } from '../../services/carrello.service';
import { PrezzoKatanaComponent } from '../shared/prezzo-katana.component';

const INTERVALLO_MS = 4000;
const PAUSA_DOPO_INTERAZIONE_MS = 8000;
const SPAZIO = 16;
@Component({
  selector: 'app-offerte-carousel',
  standalone: true,
  imports: [DatePipe, RouterLink, PrezzoKatanaComponent],
  template: `
    @if (offerte().length > 0) {
      <section
        class="offerte"
        role="region"
        aria-roledescription="carosello"
        aria-label="Offerte del momento"
        (mouseenter)="inPausa = true"
        (mouseleave)="inPausa = false"
        (focusin)="inPausa = true"
        (focusout)="inPausa = false">
        <div class="intestazione">
          <h2>Offerte del momento</h2>
          <a routerLink="/catalogo" [queryParams]="{ offerte: 1 }" class="vedi-tutte">Vedi tutte</a>
        </div>

        <div class="contenitore">
          <button type="button" class="freccia sinistra" aria-label="Offerta precedente"
                  (click)="scorri(-1); interazione()">‹</button>

          <div class="pista" #pista tabindex="0"
               (touchstart)="interazione()" (wheel)="interazione()" (pointerdown)="interazione()">
            @for (k of offerte(); track k.id) {
              <article class="scheda">
                <div class="lama">刀</div>
                <h3>{{ k.nome }}</h3>
                <span class="badge">{{ k.tipo_acciaio || 'Acciaio Tradizionale' }}</span>
                <p class="prezzo">
                  <app-prezzo-katana [prezzo]="k.prezzo" [prezzoScontato]="k.prezzoScontato" [percentuale]="k.percentualeSconto" />
                </p>
                @if (k.offertaFine) {
                  <p class="scadenza">Offerta fino al {{ k.offertaFine | date:'dd/MM/yyyy' }}</p>
                }
                <button type="button" class="btn-aggiungi"
                        [disabled]="inCarrello(k) >= k.giacenza_magazzino"
                        (click)="aggiungi(k)">
                  {{ testoPulsante(k) }}
                </button>
              </article>
            }
          </div>

          <button type="button" class="freccia destra" aria-label="Offerta successiva"
                  (click)="scorri(1); interazione()">›</button>
        </div>
      </section>
    }
  `,
  styles: [`
    :host {
      display: block;
      position: relative;
      z-index: 1;
      max-width: 1100px;
      margin: 0 auto 3rem;
      padding: 0 1rem;
    }
    .offerte {
      background: rgba(18, 18, 18, 0.9);
      border: 1px solid var(--border);
      border-radius: 12px;
      padding: 1.25rem 1.25rem 1.5rem;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
    }
    .intestazione {
      display: flex;
      align-items: baseline;
      justify-content: space-between;
      margin-bottom: 1rem;
    }
    .intestazione h2 {
      margin: 0;
    }
    .vedi-tutte {
      color: var(--gold);
      text-decoration: underline;
    }
    .contenitore {
      position: relative;
    }
    .pista {
      display: flex;
      gap: 16px;
      overflow-x: auto;
      scroll-snap-type: x mandatory;
      scrollbar-width: none;
      padding: 0.25rem;
    }
    .pista::-webkit-scrollbar {
      display: none;
    }
    .pista:focus-visible {
      outline: 2px solid var(--gold);
      outline-offset: 2px;
    }
    .scheda {
      flex: 0 0 min(280px, 78%);
      scroll-snap-align: start;
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      background: var(--surface);
      border: 1px solid var(--border);
      border-top: 3px solid var(--red);
      border-radius: 8px;
      padding: 1.25rem;
    }
    .lama {
      color: var(--gold);
      font-size: 2rem;
      line-height: 1;
      opacity: 0.35;
    }
    .scheda h3 {
      margin: 0;
      color: var(--gold);
    }
    .badge {
      align-self: flex-start;
      background: #333;
      color: var(--muted);
      font-size: 0.8rem;
      padding: 0.2rem 0.6rem;
      border-radius: 4px;
    }
    .prezzo {
      margin: 0.25rem 0 0;
      font-size: 1.4rem;
      font-weight: 700;
    }
    .scadenza {
      margin: 0;
      color: var(--muted);
      font-size: 0.85rem;
    }
    .btn-aggiungi {
      margin-top: auto;
      padding: 0.7rem;
      background: var(--red);
      color: #fff;
      border: none;
      border-radius: 6px;
      font-weight: 700;
      cursor: pointer;
      transition: background-color 0.2s ease;
    }
    .btn-aggiungi:hover:not(:disabled) {
      background: var(--red-hover);
    }
    .btn-aggiungi:disabled {
      background: #555;
      cursor: not-allowed;
    }
    .freccia {
      position: absolute;
      top: 50%;
      z-index: 2;
      transform: translateY(-50%);
      width: 2.4rem;
      height: 2.4rem;
      border-radius: 50%;
      border: 1px solid var(--border);
      background: rgba(18, 18, 18, 0.85);
      color: var(--text);
      font-size: 1.5rem;
      line-height: 1;
      cursor: pointer;
    }
    .freccia:hover {
      border-color: var(--gold);
      color: var(--gold);
    }
    .sinistra {
      left: -0.6rem;
    }
    .destra {
      right: -0.6rem;
    }
    @media (max-width: 600px) {
      .sinistra {
        left: -0.3rem;
      }
      .destra {
        right: -0.3rem;
      }
    }
  `]
})
export class OfferteCarouselComponent implements OnInit {
  private http = inject(HttpClient);
  private carrello = inject(CarrelloService);
  private platformId = inject(PLATFORM_ID);
  private destroyRef = inject(DestroyRef);

  offerte = signal<Katana[]>([]);
  esiti = signal<Record<number, string>>({});
  pista = viewChild<ElementRef<HTMLElement>>('pista');

  inPausa = false;
  private ultimaInterazione = 0;
  private timerAuto: ReturnType<typeof setInterval> | undefined;

  ngOnInit(): void {
    this.http.get<Katana[]>('http://localhost:8080/api/katane/offerte').subscribe({
      next: (dati) => {
        this.offerte.set(dati);
        this.avviaScorrimentoAutomatico();
      },
      error: () => this.offerte.set([])
    });
    this.destroyRef.onDestroy(() => clearInterval(this.timerAuto));
  }

  interazione(): void {
    this.ultimaInterazione = Date.now();
  }

  scorri(direzione: 1 | -1): void {
    const el = this.pista()?.nativeElement;
    if (!el) return;

    const riduciMovimento = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    const behavior: ScrollBehavior = riduciMovimento ? 'auto' : 'smooth';
    const scheda = el.querySelector<HTMLElement>('.scheda');
    const passo = scheda ? scheda.offsetWidth + SPAZIO : el.clientWidth * 0.8;

    const alla_fine = el.scrollLeft + el.clientWidth >= el.scrollWidth - 4;
    const all_inizio = el.scrollLeft <= 4;

    if (direzione === 1 && alla_fine) {
      el.scrollTo({ left: 0, behavior });
    } else if (direzione === -1 && all_inizio) {
      el.scrollTo({ left: el.scrollWidth, behavior });
    } else {
      el.scrollBy({ left: direzione * passo, behavior });
    }
  }

  inCarrello(k: Katana): number {
    return this.carrello.righe().find(r => r.id === k.id)?.quantita ?? 0;
  }

  testoPulsante(k: Katana): string {
    if (k.id != null && this.esiti()[k.id]) return this.esiti()[k.id];
    return this.inCarrello(k) >= k.giacenza_magazzino ? 'Massimo nel carrello' : 'Aggiungi al carrello';
  }

  async aggiungi(k: Katana): Promise<void> {
    if (k.id == null) return;
    const id = k.id;
    const esito = await this.carrello.aggiungi(k, 1);
    this.esiti.update(e => ({ ...e, [id]: esito.aggiunta ? 'Aggiunta ✓' : esito.messaggio }));
    setTimeout(() => {
      this.esiti.update(e => {
        const copia = { ...e };
        delete copia[id];
        return copia;
      });
    }, 2000);
  }

  private avviaScorrimentoAutomatico(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;

    clearInterval(this.timerAuto);
    this.timerAuto = setInterval(() => {
      if (this.inPausa || document.hidden) return;
      if (Date.now() - this.ultimaInterazione < PAUSA_DOPO_INTERAZIONE_MS) return;
      this.scorri(1);
    }, INTERVALLO_MS);
  }
}
