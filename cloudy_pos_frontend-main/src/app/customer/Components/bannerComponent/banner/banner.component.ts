import { Component, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnimationService } from 'app/Common/services/animation.service';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { Router } from '@angular/router';

gsap.registerPlugin(ScrollTrigger);

@Component({
  selector: 'app-banner',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './banner.component.html',
  styleUrls: ['./banner.component.css'],
})
export class BannerComponent implements AfterViewInit {
  bannerImg = 'assets/banner-img.png';
  @ViewChild('heroImage', { static: false })
  heroImage!: ElementRef<HTMLDivElement>;
  @ViewChild('offerHeroImg', { static: false })
  offerHeroImg!: ElementRef<HTMLImageElement>;

  constructor(
    private animationService: AnimationService,
    private router: Router,
  ) {}

  ngAfterViewInit(): void {
    this.initBannerAnimations();
    // Determine screen size for responsive animation offsets
    const isMobile = window.innerWidth <= 768;
    const slideOffset = isMobile ? 30 : 50; // Smaller offsets for mobile
    const circleOffset = isMobile ? 20 : 30;

    // Typewriter effect for h1
    this.typewriterEffect();

    // Scroll-driven slide-in animations (repeat on scroll down and up)
    // Button: Slide in from bottom
    gsap.fromTo(
      '.btn-scan',
      { y: slideOffset, opacity: 0 },
      {
        y: 0,
        opacity: 1,
        duration: isMobile ? 0.8 : 1,
        ease: 'power3.out',
        delay: isMobile ? 0.3 : 0.5,
        scrollTrigger: {
          trigger: '.banner-section',
          start: isMobile ? 'top 90%' : 'top 80%',
          end: isMobile ? 'bottom 10%' : 'bottom 20%',
          toggleActions: 'restart none none reverse',
        },
      },
    );

    // Paragraph: Slide in from left
    gsap.fromTo(
      '.banner-text p',
      { x: -slideOffset, opacity: 0 },
      {
        x: 0,
        opacity: 1,
        duration: isMobile ? 0.8 : 1,
        ease: 'power3.out',
        scrollTrigger: {
          trigger: '.banner-section',
          start: isMobile ? 'top 90%' : 'top 80%',
          end: isMobile ? 'bottom 10%' : 'bottom 20%',
          toggleActions: 'restart none none reverse',
        },
      },
    );

    // Banner Image: Slide in from right
    gsap.fromTo(
      '.circular-img img',
      { x: slideOffset, opacity: 0 },
      {
        x: 0,
        opacity: 1,
        duration: isMobile ? 1 : 1.2,
        ease: 'power3.out',
        delay: isMobile ? 0.1 : 0.2,
        scrollTrigger: {
          trigger: '.banner-section',
          start: isMobile ? 'top 90%' : 'top 80%',
          end: isMobile ? 'bottom 10%' : 'bottom 20%',
          toggleActions: 'restart none none reverse',
        },
      },
    );

    // Image Circle: Slide in with slight offset
    gsap.fromTo(
      '.circular-img-circle',
      { x: circleOffset, y: circleOffset, opacity: 0 },
      {
        x: 0,
        y: 0,
        opacity: 1,
        duration: isMobile ? 1.2 : 1.4,
        ease: 'power3.out',
        scrollTrigger: {
          trigger: '.banner-section',
          start: isMobile ? 'top 90%' : 'top 80%',
          end: isMobile ? 'bottom 10%' : 'bottom 20%',
          toggleActions: 'restart none none reverse',
        },
      },
    );

    // Existing animations (adjusted for responsiveness)
    this.animationService.fadeIn('.banner-text h2', isMobile ? 1 : 1.2);
    this.animationService.pulse('.btn-scan', {
      scale: isMobile ? 1.03 : 1.05,
      repeat: -1,
      duration: isMobile ? 0.6 : 0.8,
    });
    this.animationService.scrollZoomIn('.circular-img', {
      scaleFrom: isMobile ? 0.95 : 0.9,
      duration: isMobile ? 1.2 : 1.5,
      start: isMobile ? 'top 90%' : 'top 80%',
    });
    this.animationService.parallaxMove('.circular-img img', {
      distance: isMobile ? 30 : 50,
    });
    this.animationService.staggerScrollAppear('.bubble-animation-item', {
      stagger: isMobile ? 0.3 : 0.4,
      y: isMobile ? 20 : 30,
      duration: isMobile ? 1 : 1.2,
      start: isMobile ? 'top 95%' : 'top 90%',
    });
  }

  typewriterEffect(): void {
    const title = document.querySelector('.banner-text h1');
    if (!title) return;

    const text = title.textContent || '';
    title.textContent = '';

    // Create cursor
    const cursor = document.createElement('span');
    cursor.className = 'cursor';
    cursor.textContent = '|';
    title.appendChild(cursor);

    // Keep cursor blinking indefinitely
    gsap.to(cursor, {
      opacity: 0,
      repeat: -1,
      yoyo: true,
      duration: window.innerWidth <= 768 ? 0.4 : 0.5,
      ease: 'power1.inOut',
    });

    // GSAP Timeline for infinite typing/erasing
    const tl = gsap.timeline({ repeat: -1, repeatDelay: 1 });

    // Typing animation
    tl.to(title, {
      duration: text.length * (window.innerWidth <= 768 ? 0.04 : 0.05),
      onUpdate: function () {
        const charIndex = Math.round(this[`progress`]() * text.length);
        title.textContent = text.slice(0, charIndex);
        title.appendChild(cursor);
      },
      ease: 'none',
    });

    // Pause to show full text
    tl.to({}, { duration: window.innerWidth <= 768 ? 1 : 1.5 });

    // Erasing animation
    tl.to(title, {
      duration: text.length * (window.innerWidth <= 768 ? 0.04 : 0.05),
      onUpdate: function () {
        const charIndex = Math.round((1 - this[`progress`]()) * text.length);
        title.textContent = text.slice(0, charIndex);
        title.appendChild(cursor);
      },
      ease: 'none',
    });

    // Brief pause before restarting
    tl.to({}, { duration: window.innerWidth <= 768 ? 0.3 : 0.5 });
  }

  onScanClick(): void {
    console.log('Scan button clicked!');
    this.router.navigate(['/scan']);
  }
  private initBannerAnimations(): void {
    this.animationService.bannerAnimation(
      this.offerHeroImg.nativeElement,
      {
        h1: '.h-1',
        h2: '.h-2',
        h3: '.h-3',
      },
      this.heroImage.nativeElement,
      {
        imageScale: 1,
        imageWidth: '70%',
        h1X: '30%',
        h2X: '-50%',
        h3Y: '50%',
        start: 'top center',
        end: 'bottom center',
        scrub: 3,
      },
    );
  }
}
