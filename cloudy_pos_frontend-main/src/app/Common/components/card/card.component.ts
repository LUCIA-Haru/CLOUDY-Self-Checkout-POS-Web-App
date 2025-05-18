import {
  Component,
  EventEmitter,
  Input,
  Output,
  ElementRef,
  AfterViewInit,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { gsap } from 'gsap';

@Component({
  selector: 'app-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.css'],
})
export class CardComponent implements AfterViewInit {
  @Input() image: string = '/assets/placeholder.jpg';
  @Input() title: string = 'No Title';
  @Input() description: string = 'No description available';
  @Input() price: number = 0;
  @Input() discount?: number;
  @Input() isPercentage: boolean = false;
  @Input() currency: string = '$';
  @Output() clicked = new EventEmitter<void>();

  constructor(private elementRef: ElementRef) {}

  ngAfterViewInit(): void {
    this.initAnimations();
  }

  private initAnimations(): void {
    const card = this.elementRef.nativeElement.querySelector('.card');
    if (!card) return;

    gsap.set(card, { y: 20, opacity: 0, scale: 0.98 });
    gsap.to(card, {
      y: 0,
      opacity: 1,
      scale: 1,
      duration: 0.5,
      ease: 'power2.out',
      delay: this.getStaggerDelay(),
    });

    if (window.innerWidth > 768) {
      // Enable hover only on desktop
      card.addEventListener('mouseenter', () => this.onHover());
      card.addEventListener('mouseleave', () => this.onHoverEnd());
    }
  }

  private getStaggerDelay(): number {
    const cards = document.querySelectorAll('.card');
    const card = this.elementRef.nativeElement.querySelector('.card');
    const index = Array.from(cards).indexOf(card);
    return index >= 0 ? index * 0.1 : 0;
  }

  private onHover(): void {
    const card = this.elementRef.nativeElement.querySelector('.card');
    gsap.to(card, {
      y: -5,
      scale: 1.02,
      duration: 0.2,
      ease: 'power2.out',
    });

    if (this.discount) {
      gsap.from(
        this.elementRef.nativeElement.querySelector('.discount-badge'),
        {
          scale: 0.8,
          y: 5,
          duration: 0.3,
          ease: 'elastic.out(1, 0.5)',
        },
      );
    }
  }

  private onHoverEnd(): void {
    gsap.to(this.elementRef.nativeElement.querySelector('.card'), {
      y: 0,
      scale: 1,
      duration: 0.3,
      ease: 'power2.out',
    });
  }

  onClick(): void {
    const card = this.elementRef.nativeElement.querySelector('.card');
    gsap.to(card, {
      scale: 0.95,
      duration: 0.1,
      yoyo: true,
      repeat: 1,
      onComplete: () => this.clicked.emit(),
    });
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = '/assets/placeholder.jpg';
  }
}
