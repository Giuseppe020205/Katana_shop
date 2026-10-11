import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse, UtenteAutenticato } from '../models/auth.model';

const CHIAVE_TOKEN = 'katana-token';
const CHIAVE_UTENTE = 'katana-utente';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);

  private apiUrl = 'http://localhost:8080/api/auth';

  private tokenSignal = signal<string | null>(null);
  private utenteSignal = signal<UtenteAutenticato | null>(null);

  readonly utente = this.utenteSignal.asReadonly();
  readonly loggato = computed(() => this.tokenSignal() !== null && this.utenteSignal() !== null);

  constructor() {
    this.ripristinaSessione();
  }

  get token(): string | null {
    return this.tokenSignal();
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/login`, { username, password })
      .pipe(tap((res) => this.salvaSessione(res)));
  }

  registra(username: string, email: string, password: string): Observable<UtenteAutenticato> {
    return this.http.post<UtenteAutenticato>(`${this.apiUrl}/register`, { username, email, password });
  }

  utenteCorrente(): Observable<UtenteAutenticato> {
    return this.http.get<UtenteAutenticato>(`${this.apiUrl}/me`);
  }

  // Uscita voluta dall'utente (pulsanti "Esci")
  esci(): void {
    this.logout();
  }

  logout(reindirizza = true): void {
    this.tokenSignal.set(null);
    this.utenteSignal.set(null);
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(CHIAVE_TOKEN);
      localStorage.removeItem(CHIAVE_UTENTE);
    }
    if (reindirizza) {
      this.router.navigate(['/']);
    }
  }

  private salvaSessione(res: AuthResponse): void {
    this.tokenSignal.set(res.token);
    this.utenteSignal.set(res.utente);
    if (isPlatformBrowser(this.platformId)) {
      try {
        localStorage.setItem(CHIAVE_TOKEN, res.token);
        localStorage.setItem(CHIAVE_UTENTE, JSON.stringify(res.utente));
      } catch { /* la sessione resta comunque in memoria */ }
    }
  }

  private ripristinaSessione(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    try {
      const token = localStorage.getItem(CHIAVE_TOKEN);
      const salvato = localStorage.getItem(CHIAVE_UTENTE);
      if (token && salvato && !this.scaduto(token)) {
        this.tokenSignal.set(token);
        this.utenteSignal.set(JSON.parse(salvato));
      } else {
        localStorage.removeItem(CHIAVE_TOKEN);
        localStorage.removeItem(CHIAVE_UTENTE);
      }
    } catch { /* dati non leggibili: si riparte da non loggato */ }
  }

  private scaduto(token: string): boolean {
    try {
      const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const { exp } = JSON.parse(atob(payload));
      return typeof exp === 'number' && exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}
