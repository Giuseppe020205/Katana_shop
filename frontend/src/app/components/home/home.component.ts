import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { OfferteCarouselComponent } from './offerte-carousel.component';
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, OfferteCarouselComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {}
