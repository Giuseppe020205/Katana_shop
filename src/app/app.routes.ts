import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { CatalogoComponent } from './components/catalogo/catalogo.component';
import { OrdineComponent } from './components/ordine/ordine.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'catalogo', component: CatalogoComponent },
  { path: 'ordine', component: OrdineComponent },
  { path: 'ordine/:id', component: OrdineComponent },
  { path: '**', redirectTo: '' }
];
