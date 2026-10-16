import { Component, HostListener, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CarrelloService } from './services/carrello.service';
import { AuthService} from './services/auth.service';
@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly carrello = inject(CarrelloService);
  protected readonly auth= inject(AuthService);

  protected readonly menuAperto = signal(false);

  protected toggleMenu(): void {
    this.menuAperto.update((aperto) => !aperto);
  }

  protected chiudiMenu(): void {
    this.menuAperto.set(false);
  }

  @HostListener('document:keydown.escape')
  protected onEsc(): void {
    this.chiudiMenu();
  }
}
