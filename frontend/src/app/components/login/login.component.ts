import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { messaggioErrore, valoreInput } from '../../utils/errori';
import { BackButtonComponent } from '../shared/back-button.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, BackButtonComponent],
  template: `
    <div class="auth-sfondo"></div>
    <div class="auth-box">
      <app-back-button />
      <h2>Accedi</h2>

      @if (sessioneScaduta) {
        <div class="auth-info">La sessione è scaduta: accedi di nuovo.</div>
      }
      @if (errore()) {
        <div class="auth-errore">{{ errore() }}</div>
      }

      <div class="auth-gruppo">
        <label for="username">Username</label>
        <input id="username" class="auth-campo" type="text" autocomplete="username"
               [value]="username()" (input)="username.set(valore($event))" />
      </div>

      <div class="auth-gruppo">
        <label for="password">Password</label>
        <input id="password" class="auth-campo" type="password" autocomplete="current-password"
               [value]="password()" (input)="password.set(valore($event))" (keyup.enter)="accedi()" />
      </div>

      <button type="button" class="auth-btn" [disabled]="invio()" (click)="accedi()">
        {{ invio() ? 'Accesso in corso...' : 'Accedi' }}
      </button>

      <p class="auth-alt">Non hai un account? <a routerLink="/registrazione">Registrati</a></p>
    </div>
  `
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  username = signal('');
  password = signal('');
  errore = signal<string | null>(null);
  invio = signal(false);
  sessioneScaduta = this.route.snapshot.queryParamMap.has('sessioneScaduta');
  valore = valoreInput;

  accedi(): void {
    if (!this.username().trim() || !this.password()) {
      this.errore.set('Inserisci username e password.');
      return;
    }
    this.errore.set(null);
    this.invio.set(true);

    this.auth.login(this.username().trim(), this.password()).subscribe({
      next: () => {
        this.invio.set(false);
        const destinazione = this.route.snapshot.queryParamMap.get('returnUrl');
        const sicura = destinazione && destinazione.startsWith('/') && !destinazione.startsWith('//');
        this.router.navigateByUrl(sicura ? destinazione : '/');
      },
      error: (err) => {
        this.invio.set(false);
        this.errore.set(messaggioErrore(err, 'Username o password errati.'));
      }
    });
  }
}
