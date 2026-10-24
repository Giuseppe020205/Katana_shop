
import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { CatalogoComponent } from './components/catalogo/catalogo.component';
import { CarrelloComponent } from './components/carrello/carrello.component';
import { LoginComponent } from './components/login/login.component';
import { RegistrazioneComponent } from './components/registrazione/registrazione.component';
import { ProfiloComponent } from './components/profilo/profilo.component';
import { MieiOrdiniComponent } from './components/miei-ordini/miei-ordini.component';
import { authGuard } from './guards/auth.guard';
import { PagamentoSuccessoComponent } from './components/pagamento/pagamento-successo.component';
import { PagamentoAnnullatoComponent } from './components/pagamento/pagamento-annullato.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'catalogo', component: CatalogoComponent },
  { path: 'carrello', component: CarrelloComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registrazione', component: RegistrazioneComponent },
  { path: 'pagamento/successo', component: PagamentoSuccessoComponent },
  { path: 'pagamento/annullato', component: PagamentoAnnullatoComponent },
  { path: 'profilo', component: ProfiloComponent, canActivate: [authGuard] },
  { path: 'miei-ordini', component: MieiOrdiniComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
