import {Component,inject} from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-back-button',
  standalone: true,
  template: `<button type="button" class="back-btn" (click)="indietro()">← Indietro</button>`,
  styles: [`
      .back-btn {
        background: transparent;
        color: var(--muted);
        border: 1px solid var(--border);
        border-radius: 6px;
        padding: 0.45rem 1rem;
        cursor: pointer;
        margin-bottom: 1rem;
        transition: color 0.2s ease, border-color 0.2s ease;
      }
      .back-btn:hover {
        color: var(--gold);
        border-color: var(--gold);
      }
    `]
  })
export class BackButtonComponent {
  private location= inject(Location);
  private router=inject(Router);

  indietro(): void{
    if(window.history.length>1){
      this.location.back();

      }else{
        this.router.navigate(['/']);
      }

  }
}
