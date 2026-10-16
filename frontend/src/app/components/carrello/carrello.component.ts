import { Component, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CarrelloService, RigaCarrello } from '../../services/carrello.service';
import { AuthService } from '../../services/auth.service';
import { PagamentoService } from '../../services/pagamento.service';
import { messaggioErrore } from '../../utils/errori';
import { BackButtonComponent } from '../shared/back-button.component';
import { PrezzoKatanaComponent } from '../shared/prezzo-katana.component';
import { KatanaImmagineComponent } from '../shared/katana-immagine.component';

@Component({
  selector: 'app-carrello',
  standalone: true,
  imports: [DecimalPipe, RouterLink, BackButtonComponent, PrezzoKatanaComponent, KatanaImmagineComponent],
  templateUrl: './carrello.component.html',
  styleUrls: ['./carrello.component.css']
})
export class CarrelloComponent {
  carrello = inject(CarrelloService);
  private auth = inject(AuthService);
  private pagamento = inject(PagamentoService);
  private router = inject(Router);

  pagamentoInCorso = signal(false);
  erroreCheckout = signal<string | null>(null);

  incrementa(r: RigaCarrello): void {
    this.carrello.cambiaQuantita(r.id, r.quantita + 1);
  }

  decrementa(r: RigaCarrello): void {
    this.carrello.cambiaQuantita(r.id, r.quantita - 1);
  }

  procediAlPagamento(): void {

    if (!this.auth.loggato()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/carrello' } });
      return;
    }

    this.erroreCheckout.set(null);
    this.pagamentoInCorso.set(true);
    this.pagamento.avviaCheckout().subscribe({
      next: (res) => window.location.assign(res.url),
      error: (err) => {
        this.pagamentoInCorso.set(false);
        this.erroreCheckout.set(messaggioErrore(err, 'Impossibile avviare il pagamento.'));
        this.carrello.ricarica();
      }
    });
  }
}
