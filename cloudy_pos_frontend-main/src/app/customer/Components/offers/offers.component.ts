import {
  Component,
  ElementRef,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { AnimationService } from 'app/Common/services/animation.service';
import { GuideSectionComponent } from '../guide-section/guide-section.component';

@Component({
  selector: 'app-offers',
  imports: [],
  templateUrl: './offers.component.html',
  styleUrls: ['./offers.component.css'],
})
export class OffersComponent {
  @ViewChild('heroImage', { static: false })
  heroImage!: ElementRef<HTMLDivElement>;
  @ViewChild('offerHeroImg', { static: false })
  offerHeroImg!: ElementRef<HTMLImageElement>;
  @ViewChild('sectionTitle', { static: false }) sectionTitle!: ElementRef;
  @ViewChild('sectionSubtitle', { static: false }) sectionSubtitle!: ElementRef;
  @ViewChildren('offerCard') offerCards!: QueryList<ElementRef>;
  @ViewChildren('ctaButton') ctaButtons!: QueryList<ElementRef>;

  constructor(private animationService: AnimationService) {}

  ngAfterViewInit(): void {
    this.initBannerAnimations();
    this.offerSection();
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

  offerSection(): void {
    // Animate section title and subtitle
    this.animationService.staggerScrollAppear(
      [this.sectionTitle.nativeElement, this.sectionSubtitle.nativeElement],
      {
        duration: 1,
        y: 30,
        stagger: 0.3,
        start: 'top 80%',
      },
    );

    // Animate offer cards with zoom-in effect
    this.offerCards.forEach((card) => {
      this.animationService.scrollZoomIn(card.nativeElement, {
        duration: 1,
        scaleFrom: 0.9,
        start: 'top 80%',
      });

      // Apply 3D tilt effect on hover
      this.animationService.cardTilt(card.nativeElement, {
        maxTilt: 8,
        duration: 0.3,
      });
    });

    // Apply hover pulse to CTA buttons
    this.ctaButtons.forEach((button) => {
      this.animationService.hoverPulse(button.nativeElement, {
        duration: 0.3,
        scale: 1.1,
      });
    });
  }
}
