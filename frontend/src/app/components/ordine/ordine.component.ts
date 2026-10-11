import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Katana } from '../../models/katana.model';
import { BackButtonComponent } from '../shared/back-button.component';

@Component({
  selector: 'app-ordine',
  standalone: true,
  imports: [CommonModule, FormsModule, BackButtonComponent],
  templateUrl: './ordine.component.html',
  styleUrls: ['./ordine.component.css']
})
export class OrdineComponent implements OnInit {
  katanaId: number | null = null;
  katana: Katana | null = null;
  quantita: number = 1;
  messaggioEsito: string | null = null;
  isSuccesso: boolean = false;
  invioInCorso: boolean = false;

  private apiUrl = 'http://localhost:8080/api/katane';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.katanaId = Number(id);
      this.http.get<Katana>(`${this.apiUrl}/${this.katanaId}`).subscribe({
        next: (k) => (this.katana = k),
        error: () => {
          this.isSuccesso = false;
          this.messaggioEsito = 'Katana non trovata.';
        }
      });
    }
  }

  confermaAcquisto(): void {
    if (!this.katanaId) {
      this.messaggioEsito = 'Nessuna katana selezionata.';
      return;
    }
    if (this.quantita < 1) {
      this.isSuccesso = false;
      this.messaggioEsito = 'La quantità minima è 1.';
      return;
    }

    this.invioInCorso = true;
    const body = { idKatana: this.katanaId, quantita: this.quantita };

    this.http.post<{ messaggio: string }>(`${this.apiUrl}/acquista`, body).subscribe({
      next: (res) => {
        this.invioInCorso = false;
        this.isSuccesso = true;
        this.messaggioEsito = res.messaggio;
        setTimeout(() => this.router.navigate(['/catalogo']), 2000);
      },
      error: (err) => {
        this.invioInCorso = false;
        this.isSuccesso = false;
        this.messaggioEsito = err.error?.messaggio || "Errore durante il completamento dell'ordine.";
      }
    });
  }
}
