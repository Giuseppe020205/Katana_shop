import { Component, input } from '@angular/core';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-prezzo-katana',
  standalone: true,
  imports: [DecimalPipe],
  template: `
    @if (prezzoScontato() != null && percentuale()) {
      <del class="originale">€ {{ prezzo() | number:'1.2-2' }}</del>
      <span class="scontato">€ {{ prezzoScontato() | number:'1.2-2' }}</span>
      <span class="percentuale">-{{ percentuale() }}%</span>
    } @else {
      <span class="normale">€ {{ prezzo() | number:'1.2-2' }}</span>
    }
  `,
  styles: [`
    :host {
      display: inline-flex;
      align-items: baseline;
      flex-wrap: wrap;
      gap: 0.2rem 0.6rem;
    }
    .originale {
      color: var(--muted);
      font-size: 0.7em;
      font-weight: 400;
      text-decoration: line-through;
      text-decoration-thickness: 2px;
      text-decoration-color: var(--red-hover);
    }
    .scontato {
      color: var(--gold);
      font-weight: 700;
    }
    .percentuale {
      align-self: center;
      background: var(--red);
      color: #fff;
      font-size: 0.6em;
      font-weight: 700;
      padding: 0.1rem 0.45rem;
      border-radius: 999px;
    }
  `]
})
export class PrezzoKatanaComponent {
  prezzo = input.required<number>();
  prezzoScontato = input<number | null | undefined>(null);
  percentuale = input<number | null | undefined>(null);
}
