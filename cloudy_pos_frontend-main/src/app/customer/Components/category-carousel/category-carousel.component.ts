import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { AnimationService } from 'app/Common/services/animation.service';

@Component({
  selector: 'app-category-carousel',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './category-carousel.component.html',
  styleUrls: ['./category-carousel.component.css'],
})
export class CategoryCarouselComponent implements AfterViewInit {
  @ViewChild('carouselTrack') carouselTrack!: ElementRef;

  items = [
    { src: 'assets/category/bakery.png', alt: 'Bakery' },
    { src: 'assets/category/beverage.png', alt: 'Beverage' },
    { src: 'assets/category/canedFood.png', alt: 'Canned Food' },
    { src: 'assets/category/egg.png', alt: 'Eggs' },
    { src: 'assets/category/icecream.png', alt: 'Ice Cream' },
    { src: 'assets/category/kitchware.png', alt: 'Kitchenware' },
    { src: 'assets/category/meat.png', alt: 'Meat' },
    { src: 'assets/category/egg.png', alt: 'Eggs' },
  ];

  constructor(private animationService: AnimationService) {}

  ngAfterViewInit(): void {
    if (!this.carouselTrack || !this.carouselTrack.nativeElement) {
      console.error('Carousel track element not found!');
      return;
    }

    // Get all carousel items
    const track = this.carouselTrack.nativeElement;
    const items = Array.from(
      track.querySelectorAll('.carousel-item'),
    ) as HTMLElement[];

    // Initialize the infinite carousel
    this.animationService.createInfiniteCarousel(track, items, {
      speed: 0.5, // Speed of scrolling (adjust as needed)
      start: 'top 90%',
      end: 'bottom 10%',
      pauseOnHover: true, // Enable pause on hover
    });
    //////////////////////////////////////////

    // Animate Main Title
    this.animationService.scrollFadeIn('#offers-title', {
      duration: 1,
      y: 30,
      start: 'top 90%',
    });

    // Coupons Section Animations
    this.animationService.scrubSlideFromLeft('#coupon-text', {
      distance: 150,
      start: 'top 80%',
      end: 'bottom 20%',
    });

    this.animationService.scrubSlideFromRight('#coupon-image', {
      distance: 150,
      start: 'top 80%',
      end: 'bottom 20%',
    });

    // Loyalty Points Section Animations
    this.animationService.scrubSlideFromLeft('#loyalty-text', {
      distance: 150,
      start: 'top 80%',
      end: 'bottom 20%',
    });

    this.animationService.scrubSlideFromRight('#loyalty-image', {
      distance: 150,
      start: 'top 80%',
      end: 'bottom 20%',
    });
  }
}
