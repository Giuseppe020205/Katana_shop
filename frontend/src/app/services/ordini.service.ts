import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type StatoOrdine = 'IN_ATTESA_PAGAMENTO' | 'PAGATO' | 'ANNULLATO';

export interface RigaOrdineStorico {
  katanaId: number;
  nome: string;
  immagine: string | null;
  quantita: number;
  prezzoUnitario: number;
  subtotale: number;
}

export interface OrdineStorico {
  id: number;
  stato: StatoOrdine;
  dataOrdine: string;
  totale: number;
  righe: RigaOrdineStorico[];
}

const API = 'http://localhost:8080/api/ordini';

@Injectable({ providedIn: 'root' })
export class OrdiniService {
  private http = inject(HttpClient);

  mieiOrdini(): Observable<OrdineStorico[]> {
    return this.http.get<OrdineStorico[]>(API);
  }
}
