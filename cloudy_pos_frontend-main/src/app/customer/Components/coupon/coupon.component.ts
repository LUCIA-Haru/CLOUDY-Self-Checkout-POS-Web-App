import { CommonModule } from '@angular/common';
import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ElementRef,
} from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ApiService } from 'app/customer/apiService/api.service';
import { AnimationService } from 'app/Common/services/animation.service';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { SkeletonLoaderComponent } from 'app/Common/components/skeleton-loader/skeleton-loader.component';

interface Coupon {
  couponId: number;
  couponCode: string;
  discountAmount: number;
  minPurchaseAmount: number;
  expirationDate: Date;
  active: boolean;
}

@Component({
  selector: 'app-coupon',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    SkeletonLoaderComponent,
  ],
  templateUrl: './coupon.component.html',
  styleUrls: ['./coupon.component.css'],
})
export class CouponComponent implements OnInit, AfterViewInit {
  coupons: Coupon[] = [];
  isLoading: boolean = true;

  // @ViewChild('heroTitle', { static: false }) heroTitle!: ElementRef;

  constructor(
    private apiService: ApiService,
    private animationService: AnimationService,
  ) {
    gsap.registerPlugin(ScrollTrigger);
  }

  ngOnInit(): void {
    this.fetchCoupons();
  }

  ngAfterViewInit(): void {
    this.setupAnimations();
  }

  fetchCoupons(): void {
    this.isLoading = true;
    this.apiService.fetchCouponsHistory().subscribe({
      next: (response) => {
        this.coupons = response.data.map((coupon: Coupon) => ({
          ...coupon,
          expirationDate: new Date(coupon.expirationDate),
        }));
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching coupons:', error);
        this.isLoading = false;
      },
    });
  }

  setupAnimations(): void {
    const cards = document.querySelectorAll(
      '.coupon-card',
    ) as NodeListOf<HTMLElement>;
    const badges = document.querySelectorAll(
      '.status-badge',
    ) as NodeListOf<HTMLElement>;
    const buttons = document.querySelectorAll(
      '.btn-copy',
    ) as NodeListOf<HTMLElement>;

    if (cards.length > 0) {
      this.animationService.staggerScrollAppear(cards, { stagger: 0.3 });
      cards.forEach((item: HTMLElement) => {
        item.addEventListener('mouseenter', () => {
          gsap.to(item, {
            scale: 1.05,
            duration: 0.1,
            ease: 'power2.out',
          });
        });
        item.addEventListener('mouseleave', () => {
          gsap.to(item, {
            scale: 1,
            duration: 0.1,
            ease: 'power2.out',
          });
        });
      });
    }

    if (badges.length > 0) {
      badges.forEach((badge) => {
        this.animationService.pulse(badge, { scale: 1.1, duration: 1.5 });
      });
    }

    if (buttons.length > 0) {
      buttons.forEach((button) => {
        this.animationService.bounceIn(button);
      });
    }
  }

  copyCode(code: string): void {
    navigator.clipboard
      .writeText(code)
      .then(() => {
        alert(`Coupon code ${code} copied to clipboard!`);
      })
      .catch((err) => {
        console.error('Failed to copy code:', err);
        alert('Failed to copy coupon code.');
      });
  }
}
