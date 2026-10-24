import { Component, input, signal } from '@angular/core';

@Component({
  selector: 'app-katana-immagine',
  standalone: true,
  template: `
    @if (src() && srcFallita() !== src()) {
      <img
        [src]="src()!"
        [alt]="alt()"
        loading="lazy"
        decoding="async"
        (error)="srcFallita.set(src() ?? null)" />
    } @else {
      <span class="segnaposto" aria-hidden="true">刀</span>
    }
  `,
  styles: [`
    :host {
      display: block;
      width: 100%;
      aspect-ratio: 4 / 3;
      background: #151515;
      border-radius: 6px;
      overflow: hidden;
    }
    img {
      display: block;
      width: 100%;
      height: 100%;
      object-fit: var(--adattamento-immagine, contain);
    }
    .segnaposto {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
      color: var(--gold);
      font-size: 2.5rem;
      opacity: 0.35;
    }
  `]
})
export class KatanaImmagineComponent {
  src = input<string | null | undefined>(null);
  alt = input<string>('');
  protected srcFallita = signal<string | null>(null);
}
