import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Katana } from '../../models/katana.model';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './catalogo.component.html',
  styleUrls: ['./catalogo.component.css']
})
export class CatalogoComponent implements OnInit {
  katane: Katana[] = [];
  caricamento: boolean = true;
  errore: string | null = null;

  private apiUrl = 'http://localhost:8080/katane';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.caricaKatane();
  }

  caricaKatane(): void {
    this.http.get<Katana[]>(this.apiUrl).subscribe({
      next: (data) => {
        this.katane = data;
        this.caricamento = false;
      },
      error: (err) => {
        this.errore = 'Impossibile caricare il catalogo katane.';
        this.caricamento = false;
        console.error(err);
      }
    });
  }

  selezionaPerAcquisto(katana: Katana): void {
    this.router.navigate(['/ordine', katana.id]);
  }
}
