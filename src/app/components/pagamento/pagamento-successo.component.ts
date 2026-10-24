import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CarrelloService } from '../../services/carrello.service';

const TENTATIVI = 5;
const ATTESA_MS = 2000;

@Component({
  selector: 'app-pagamento-successo',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './pagamento-successo.component.html',
  styleUrls: ['./esito-pagamento.component.css']
})
export class PagamentoSuccessoComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private carrello = inject(CarrelloService);

  ordineId = signal<number | null>(null);
  confermato = signal(false);
  inAttesa = signal(true);

  private timer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.queryParamMap.get('ordine'));
    this.ordineId.set(Number.isInteger(id) && id > 0 ? id : null);
    this.verifica(1);
  }

  ngOnDestroy(): void {
    if (this.timer) clearTimeout(this.timer);
  }

  private async verifica(tentativo: number): Promise<void> {
    const aggiornato = await this.carrello.ricarica();
    if (aggiornato && this.carrello.righe().length === 0) {
      this.confermato.set(true);
      this.inAttesa.set(false);
    } else if (tentativo < TENTATIVI) {
      this.timer = setTimeout(() => this.verifica(tentativo + 1), ATTESA_MS);
    } else {
      this.inAttesa.set(false);
    }
  }
}
