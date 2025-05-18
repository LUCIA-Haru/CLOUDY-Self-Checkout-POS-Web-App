import { AfterViewInit, Component } from '@angular/core';
import { BannerComponent } from '../bannerComponent/banner/banner.component';
import { DiscountProductsComponent } from '../discount-products/discount-products.component';
import { RouterOutlet } from '@angular/router';
import { StaticPartsComponent } from '../static-parts/static-parts.component';
import { CategoryCarouselComponent } from '../category-carousel/category-carousel.component';
import { MatIconModule } from '@angular/material/icon';
import { AnimationService } from 'app/Common/services/animation.service';
import { PreLoaderComponent } from '../pre-loader/pre-loader.component';

@Component({
  selector: 'app-home',
  imports: [
    BannerComponent,
    DiscountProductsComponent,
    RouterOutlet,
    StaticPartsComponent,
    CategoryCarouselComponent,
    MatIconModule,
    PreLoaderComponent,
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements AfterViewInit {
  constructor(private animation: AnimationService) {}
  ngAfterViewInit(): void {
    this.animation.scrubSlideFromLeft('#img-card-holder', {
      distance: 150,
      start: 'top 80%',
      end: 'bottom 20%',
    });
    this.animation.scrubSlideFromRight('#content_holder', {
      distance: 150,
      start: 'top 80%',
      end: 'bottom 20%',
    });
  }
}
