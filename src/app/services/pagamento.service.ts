import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CheckoutResponse {
  url: string;
}

const API = 'http://localhost:8080/api/pagamenti';

@Injectable({ providedIn: 'root' })
export class PagamentoService {
  private http = inject(HttpClient);

  avviaCheckout(): Observable<CheckoutResponse> {
    return this.http.post<CheckoutResponse>(`${API}/checkout`, {});
  }
}
