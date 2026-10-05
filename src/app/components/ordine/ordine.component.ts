import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-ordine',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ordine.component.html',
  styleUrls: ['./ordine.component.css']
})
export class OrdineComponent implements OnInit {
  katanaId: string | null = null;
  quantita: number = 1;
  messaggioEsito: string | null = null;
  isSuccesso: boolean = false;
  invioInCorso: boolean = false;

  private apiUrl = 'http://localhost:8080/katane/acquista';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.katanaId = this.route.snapshot.paramMap.get('id');
  }

  confermaAcquisto(): void {
    if (!this.katanaId) {
      this.messaggioEsito = 'Nessuna katana selezionata.';
      return;
    }

    this.invioInCorso = true;
    const body = {
      idKatana: this.katanaId,
      quantita: this.quantita
    };

    this.http.post<{ success: boolean; message: string }>(this.apiUrl, body).subscribe({
      next: (res) => {
        this.invioInCorso = false;
        this.isSuccesso = res.success;
        this.messaggioEsito = res.message;
        if (res.success) {
          setTimeout(() => this.router.navigate(['/catalogo']), 2000);
        }
      },
      error: (err) => {
        this.invioInCorso = false;
        this.isSuccesso = false;
        this.messaggioEsito = err.error?.message || 'Errore durante il completamento dell\'ordine.';
      }
    });
  }
}
