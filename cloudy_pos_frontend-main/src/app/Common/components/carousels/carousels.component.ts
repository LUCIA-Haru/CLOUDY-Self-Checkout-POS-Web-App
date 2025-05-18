import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  ViewChild,
  Output,
  EventEmitter,
  ChangeDetectorRef,
  HostListener,
} from '@angular/core';
import { gsap } from 'gsap';
import { CardComponent } from '../card/card.component';
import { FullImageUrlPipe } from 'app/Common/pipe/full-imgae-url/full-image-url.pipe';
import { Router } from '@angular/router';

@Component({
  selector: 'app-carousels',
  standalone: true,
  imports: [CommonModule, CardComponent, FullImageUrlPipe],
  templateUrl: './carousels.component.html',
  styleUrls: ['./carousels.component.css'],
})
export class CarouselsComponent implements AfterViewInit {
  @Input() items: any[] = [];
  @ViewChild('carousel') carousel!: ElementRef;
  @Output() cardClicked = new EventEmitter<any>();

  currentIndex = 0;
  itemsPerSlide = 3;
  maxIndex: number = 0;

  constructor(
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngAfterViewInit(): void {
    this.updateItemsPerSlide();
    this.setupCarousel();
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.updateItemsPerSlide();
    this.scrollToCurrent();
  }

  updateItemsPerSlide(): void {
    const width = window.innerWidth;
    if (width <= 768) {
      this.itemsPerSlide = 1;
    } else if (width <= 1024) {
      this.itemsPerSlide = 2;
    } else {
      this.itemsPerSlide = 3;
    }
    this.maxIndex = Math.max(
      0,
      Math.ceil(this.items.length / this.itemsPerSlide) - 1,
    );
    this.currentIndex = Math.min(this.currentIndex, this.maxIndex);
    this.cdr.detectChanges();
  }

  setupCarousel(): void {
    const items =
      this.carousel.nativeElement.querySelectorAll('.carousel-item');
    if (!items.length) {
      console.warn('No items found in the carousel.');
      return;
    }
    this.setupAnimations(items);
    this.scrollToCurrent();
  }

  setupAnimations(items: NodeListOf<HTMLElement>): void {
    gsap.set(items, { opacity: 1, scale: 1, x: 0 });

    gsap.from(items, {
      opacity: 0,
      y: 50,
      stagger: 0.2,
      duration: 0.8,
      ease: 'power3.out',
    });

    items.forEach((item) => {
      item.addEventListener('mouseenter', () => {
        if (window.innerWidth > 768) {
          gsap.to(item, {
            scale: 1.05,
            duration: 0.3,
            ease: 'power2.out',
          });
        }
      });
      item.addEventListener('mouseleave', () => {
        gsap.to(item, {
          scale: 1,
          duration: 0.3,
          ease: 'power2.out',
        });
      });
    });
  }

  prev(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
      this.scrollToCurrent();
      this.cdr.detectChanges();
    }
  }

  next(): void {
    if (this.currentIndex < this.maxIndex) {
      this.currentIndex++;
      this.scrollToCurrent();
      this.cdr.detectChanges();
    }
  }

  scrollToCurrent(): void {
    const items =
      this.carousel.nativeElement.querySelectorAll('.carousel-item');
    if (!items.length) return;

    const itemWidth = items[0].offsetWidth;
    const gap =
      parseFloat(getComputedStyle(this.carousel.nativeElement).gap) || 20;
    const slideWidth = (itemWidth + gap) * this.itemsPerSlide;
    const containerWidth =
      this.carousel.nativeElement.parentElement.offsetWidth;

    // Simplified offset: Just move by slideWidth, no centering adjustment
    let offset = -this.currentIndex * slideWidth;

    // Ensure offset doesn't exceed bounds
    const totalCarouselWidth = (itemWidth + gap) * this.items.length - gap;
    const maxOffset = Math.max(-(totalCarouselWidth - containerWidth), 0);
    offset = Math.max(offset, maxOffset);
    offset = Math.min(offset, 0); // Prevent positive offset

    gsap.to(this.carousel.nativeElement, {
      x: offset,
      duration: 0.9,
      ease: 'power3.out',
    });

    this.animateActiveItems(items);
  }

  animateActiveItems(items: NodeListOf<HTMLElement>): void {
    const startIndex = this.currentIndex * this.itemsPerSlide;
    const endIndex = Math.min(
      startIndex + this.itemsPerSlide,
      this.items.length,
    );

    items.forEach((item, index) => {
      const isActive = index >= startIndex && index < endIndex;
      item.classList.toggle('active', isActive);
      gsap.set(item, {
        opacity: isActive ? 1 : 0.7,
        scale: isActive ? 1 : 0.95,
      });
    });
  }

  onCardClick(item: any): void {
    this.cardClicked.emit(item);
    this.router.navigate(['/product/search', item.barcode]);
  }
}
