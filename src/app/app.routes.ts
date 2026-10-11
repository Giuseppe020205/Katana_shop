import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { CatalogoComponent } from './components/catalogo/catalogo.component';
import { CarrelloComponent } from './components/carrello/carrello.component';
import { OrdineComponent } from './components/ordine/ordine.component';
import { LoginComponent } from './components/login/login.component';
import { RegistrazioneComponent } from './components/registrazione/registrazione.component';
import { ProfiloComponent } from './components/profilo/profilo.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'catalogo', component: CatalogoComponent },
  { path: 'carrello', component: CarrelloComponent },
  { path: 'ordine', component: OrdineComponent },
  { path: 'ordine/:id', component: OrdineComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registrazione', component: RegistrazioneComponent },
  { path: 'profilo', component: ProfiloComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
