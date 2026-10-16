import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

const API = 'http://localhost:8080/api/';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.token;

  const richiesta =
    token && req.url.startsWith(API)
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

  return next(richiesta).pipe(
    catchError((err: HttpErrorResponse) => {
      const rottaAccesso = req.url.includes('/api/auth/login') || req.url.includes('/api/auth/register');
      if (err.status === 401 && !rottaAccesso && auth.loggato()) {
        auth.logout(false);
        router.navigate(['/login'], { queryParams: { sessioneScaduta: 1 } });
      }
      return throwError(() => err);
    })
  );
};
