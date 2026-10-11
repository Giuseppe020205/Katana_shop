import { Component, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CarrelloService, RigaCarrello } from '../../services/carrello.service';
import { BackButtonComponent } from '../shared/back-button.component';

@Component({
  selector: 'app-carrello',
  standalone: true,
  imports: [DecimalPipe, RouterLink, BackButtonComponent],
  templateUrl: './carrello.component.html',
  styleUrls: ['./carrello.component.css']
})
export class CarrelloComponent {
  carrello = inject(CarrelloService);

  incrementa(r: RigaCarrello): void {
    this.carrello.cambiaQuantita(r.id, r.quantita + 1);
  }

  decrementa(r: RigaCarrello): void {
    this.carrello.cambiaQuantita(r.id, r.quantita - 1);
  }
}
