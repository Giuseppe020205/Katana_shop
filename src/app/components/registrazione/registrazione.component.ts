import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { switchMap } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { messaggioErrore, valoreInput } from '../../utils/errori';
import { BackButtonComponent } from '../shared/back-button.component';

@Component({
  selector: 'app-registrazione',
  standalone: true,
  imports: [RouterLink, BackButtonComponent],
  template: `
    <div class="auth-sfondo"></div>
    <div class="auth-box">
      <app-back-button />
      <h2>Crea un account</h2>

      @if (errore()) {
        <div class="auth-errore">{{ errore() }}</div>
      }

      <div class="auth-gruppo">
        <label for="username">Username</label>
        <input id="username" class="auth-campo" type="text" autocomplete="username"
               [value]="username()" (input)="username.set(valore($event))" />
      </div>

      <div class="auth-gruppo">
        <label for="email">Email</label>
        <input id="email" class="auth-campo" type="email" autocomplete="email"
               [value]="email()" (input)="email.set(valore($event))" />
      </div>

      <div class="auth-gruppo">
        <label for="password">Password (almeno 8 caratteri)</label>
        <input id="password" class="auth-campo" type="password" autocomplete="new-password"
               [value]="password()" (input)="password.set(valore($event))" />
      </div>

      <div class="auth-gruppo">
        <label for="conferma">Conferma password</label>
        <input id="conferma" class="auth-campo" type="password" autocomplete="new-password"
               [value]="conferma()" (input)="conferma.set(valore($event))" (keyup.enter)="registrati()" />
      </div>

      <button type="button" class="auth-btn" [disabled]="invio()" (click)="registrati()">
        {{ invio() ? 'Registrazione in corso...' : 'Registrati' }}
      </button>

      <p class="auth-alt">Hai già un account? <a routerLink="/login">Accedi</a></p>
    </div>
  `
})
export class RegistrazioneComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = signal('');
  email = signal('');
  password = signal('');
  conferma = signal('');
  errore = signal<string | null>(null);
  invio = signal(false);
  valore = valoreInput;

  registrati(): void {
    const username = this.username().trim();
    const email = this.email().trim();
    const password = this.password();

    if (username.length < 3 || username.length > 50) {
      this.errore.set('Lo username deve avere tra 3 e 50 caratteri.');
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      this.errore.set('Inserisci un indirizzo email valido.');
      return;
    }
    if (password.length < 8) {
      this.errore.set('La password deve avere almeno 8 caratteri.');
      return;
    }
    if (password !== this.conferma()) {
      this.errore.set('Le due password non coincidono.');
      return;
    }

    this.errore.set(null);
    this.invio.set(true);

    this.auth
      .registra(username, email, password)
      .pipe(switchMap(() => this.auth.login(username, password)))
      .subscribe({
        next: () => {
          this.invio.set(false);
          this.router.navigateByUrl('/');
        },
        error: (err) => {
          this.invio.set(false);
          this.errore.set(messaggioErrore(err, 'Registrazione non riuscita. Riprova.'));
        }
      });
  }
}
