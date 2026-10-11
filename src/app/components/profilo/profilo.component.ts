import { Component, OnInit, inject, signal } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { UtenteAutenticato } from '../../models/auth.model';
import { BackButtonComponent } from '../shared/back-button.component';

@Component({
  selector: 'app-profilo',
  standalone: true,
  imports: [BackButtonComponent],
  template: `
    <div class="auth-box">
      <app-back-button />
      <h2>Il mio profilo</h2>

      @if (errore()) {
        <div class="auth-errore">{{ errore() }}</div>
      }

      @if (utente(); as u) {
        <dl class="auth-dati">
          <dt>Username</dt><dd>{{ u.username }}</dd>
          <dt>Email</dt><dd>{{ u.email }}</dd>
          <dt>Codice utente</dt><dd>{{ u.id }}</dd>
        </dl>
      } @else if (!errore()) {
        <p>Caricamento...</p>
      }

      <button type="button" class="auth-btn secondario" (click)="auth.logout()">Esci</button>
    </div>
  `
})
export class ProfiloComponent implements OnInit {
  auth = inject(AuthService);
  utente = signal<UtenteAutenticato | null>(null);
  errore = signal<string | null>(null);

  ngOnInit(): void {
    this.auth.utenteCorrente().subscribe({
      next: (u) => this.utente.set(u),
      error: () => this.errore.set('Impossibile caricare il profilo.')
    });
  }
}
