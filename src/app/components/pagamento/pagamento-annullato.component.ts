import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CarrelloService } from '../../services/carrello.service';

@Component({
  selector: 'app-pagamento-annullato',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './pagamento-annullato.component.html',
  styleUrls: ['./esito-pagamento.component.css']
})
export class PagamentoAnnullatoComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private carrello = inject(CarrelloService);

  ordineId = signal<number | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.queryParamMap.get('ordine'));
    this.ordineId.set(Number.isInteger(id) && id > 0 ? id : null);
    this.carrello.ricarica();
  }
}
