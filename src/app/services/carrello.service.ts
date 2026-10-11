import { Injectable, PLATFORM_ID, computed, effect, inject, signal, untracked } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Katana } from '../models/katana.model';
import { UtenteAutenticato } from '../models/auth.model';
import { messaggioErrore } from '../utils/errori';
import { AuthService } from './auth.service';

export interface RigaCarrello {
  id: number;
  nome: string;
  prezzo: number;
  giacenza_magazzino: number;
  quantita: number;
  disponibile: boolean;
  avviso: string | null;
}

export interface EsitoAggiunta {
  aggiunta: boolean;
  messaggio: string;
}

// Risposta del server
interface RigaCarrelloDto {
  katanaId: number;
  nome: string;
  prezzo: number;
  giacenza_magazzino: number;
  quantita: number;
  subtotale: number;
  disponibile: boolean;
  avviso: string | null;
}

interface CarrelloDto {
  righe: RigaCarrelloDto[];
  numeroArticoli: number;
  totale: number;
  acquistabile: boolean;
}

const API = 'http://localhost:8080/api/carrello';
const CHIAVE_OSPITE = 'katana-carrello-ospite';
const LIMITE_RAGGIUNTO = 'Hai già raggiunto la quantità disponibile';

@Injectable({ providedIn: 'root' })
export class CarrelloService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private platformId = inject(PLATFORM_ID);

  private modalitaUtente = false;
  private righeSignal = signal<RigaCarrello[]>([]);
  private caricamentoSignal = signal(false);
  private erroreSignal = signal<string | null>(null);

  readonly righe = this.righeSignal.asReadonly();
  readonly caricamento = this.caricamentoSignal.asReadonly();
  readonly errore = this.erroreSignal.asReadonly();

  readonly numeroArticoli = computed(() =>
    this.righeSignal().filter(r => r.disponibile).reduce((somma, r) => somma + r.quantita, 0)
  );
  readonly totale = computed(() =>
    this.righeSignal().filter(r => r.disponibile).reduce((somma, r) => somma + r.prezzo * r.quantita, 0)
  );
  readonly acquistabile = computed(() =>
    this.righeSignal().length > 0 && this.righeSignal().every(r => r.disponibile)
  );

  constructor() {
    // Il carrello segue l'utente: a ogni login o logout cambia proprietario
    effect(() => {
      const utente = this.auth.utente();
      untracked(() => this.cambiaProprietario(utente));
    });
  }

  async aggiungi(k: Katana, quantita = 1): Promise<EsitoAggiunta> {
    if (k.id == null) {
      return { aggiunta: false, messaggio: 'Katana non valida' };
    }
    this.erroreSignal.set(null);
    const prima = this.quantitaDi(k.id);

    if (this.modalitaUtente) {
      try {
        const carrello = await firstValueFrom(
          this.http.post<CarrelloDto>(`${API}/righe`, { katanaId: k.id, quantita })
        );
        this.applica(carrello);
      } catch (err) {
        return { aggiunta: false, messaggio: messaggioErrore(err, 'Impossibile aggiungere al carrello.') };
      }
      return this.quantitaDi(k.id) > prima
        ? { aggiunta: true, messaggio: `"${k.nome}" aggiunta al carrello` }
        : { aggiunta: false, messaggio: LIMITE_RAGGIUNTO };
    }

    // Ospite: carrello nel browser
    if (prima + quantita > k.giacenza_magazzino) {
      return { aggiunta: false, messaggio: LIMITE_RAGGIUNTO };
    }
    const righe = this.righeSignal();
    const nuova: RigaCarrello = {
      id: k.id,
      nome: k.nome,
      prezzo: k.prezzo,
      giacenza_magazzino: k.giacenza_magazzino,
      quantita: prima + quantita,
      disponibile: true,
      avviso: null
    };
    this.salvaOspite(
      righe.some(r => r.id === k.id) ? righe.map(r => (r.id === k.id ? nuova : r)) : [...righe, nuova]
    );
    return { aggiunta: true, messaggio: `"${k.nome}" aggiunta al carrello` };
  }

  cambiaQuantita(id: number, quantita: number): void {
    this.erroreSignal.set(null);
    if (this.modalitaUtente) {
      this.http.put<CarrelloDto>(`${API}/righe/${id}`, { quantita }).subscribe({
        next: (c) => this.applica(c),
        error: (err) => this.erroreSignal.set(messaggioErrore(err, 'Impossibile modificare la quantità.'))
      });
      return;
    }
    this.salvaOspite(
      this.righeSignal().map(r =>
        r.id === id ? { ...r, quantita: Math.max(1, Math.min(quantita, r.giacenza_magazzino)) } : r
      )
    );
  }

  rimuovi(id: number): void {
    this.erroreSignal.set(null);
    if (this.modalitaUtente) {
      this.http.delete<CarrelloDto>(`${API}/righe/${id}`).subscribe({
        next: (c) => this.applica(c),
        error: (err) => this.erroreSignal.set(messaggioErrore(err, 'Impossibile rimuovere la katana.'))
      });
      return;
    }
    this.salvaOspite(this.righeSignal().filter(r => r.id !== id));
  }

  svuota(): void {
    this.erroreSignal.set(null);
    if (this.modalitaUtente) {
      this.http.delete<CarrelloDto>(API).subscribe({
        next: (c) => this.applica(c),
        error: (err) => this.erroreSignal.set(messaggioErrore(err, 'Impossibile svuotare il carrello.'))
      });
      return;
    }
    this.salvaOspite([]);
  }

  private quantitaDi(id: number): number {
    return this.righeSignal().find(r => r.id === id)?.quantita ?? 0;
  }

  private cambiaProprietario(utente: UtenteAutenticato | null): void {
    this.erroreSignal.set(null);

    if (!utente) {
      this.modalitaUtente = false;
      this.caricamentoSignal.set(false);
      this.righeSignal.set(this.leggiOspite());
      return;
    }

    if (!isPlatformBrowser(this.platformId)) return;

    this.modalitaUtente = true;
    this.righeSignal.set([]);
    this.caricamentoSignal.set(true);

    const daOspite = this.leggiOspite();
    const richiesta = daOspite.length > 0
      ? this.http.post<CarrelloDto>(`${API}/unisci`, {
          righe: daOspite.map(r => ({ katanaId: r.id, quantita: r.quantita }))
        })
      : this.http.get<CarrelloDto>(API);

    richiesta.subscribe({
      next: (c) => {
        // Se nel frattempo l'utente è cambiato o è uscito, la risposta non vale più
        if (this.auth.utente()?.id !== utente.id) return;
        this.scriviOspite([]);
        this.applica(c);
        this.caricamentoSignal.set(false);
      },
      error: (err) => {
        if (this.auth.utente()?.id !== utente.id) return;
        this.caricamentoSignal.set(false);
        this.erroreSignal.set(messaggioErrore(err, 'Impossibile caricare il carrello.'));
      }
    });
  }

  private applica(c: CarrelloDto): void {
    this.righeSignal.set(
      c.righe.map(r => ({
        id: r.katanaId,
        nome: r.nome,
        prezzo: r.prezzo,
        giacenza_magazzino: r.giacenza_magazzino,
        quantita: r.quantita,
        disponibile: r.disponibile,
        avviso: r.avviso
      }))
    );
  }

  private salvaOspite(righe: RigaCarrello[]): void {
    this.righeSignal.set(righe);
    this.scriviOspite(righe);
  }

  private scriviOspite(righe: RigaCarrello[]): void {
    if (!isPlatformBrowser(this.platformId)) return;
    try {
      localStorage.setItem(CHIAVE_OSPITE, JSON.stringify(righe));
    } catch { /* spazio non disponibile: il carrello resta comunque in memoria */ }
  }

  private leggiOspite(): RigaCarrello[] {
    if (!isPlatformBrowser(this.platformId)) return [];
    try {
      const salvato = localStorage.getItem(CHIAVE_OSPITE);
      const righe: RigaCarrello[] = salvato ? JSON.parse(salvato) : [];
      return righe.map(r => ({ ...r, disponibile: true, avviso: null }));
    } catch {
      return [];
    }
  }
}
