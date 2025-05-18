import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { AnimationService } from 'app/Common/services/animation.service';

@Component({
  selector: 'app-static-parts',
  imports: [],
  templateUrl: './static-parts.component.html',
  styleUrl: './static-parts.component.css',
})
export class StaticPartsComponent implements AfterViewInit {
  @ViewChild('selfTitle') selfTitle!: ElementRef;
  @ViewChild('leftFeature') leftFeature!: ElementRef;
  @ViewChild('centerFeature') centerFeature!: ElementRef;
  @ViewChild('rightFeature') rightFeature!: ElementRef;

  constructor(private animationService: AnimationService) {}

  ngAfterViewInit(): void {
    // Fade in the title
    this.animationService.scrollFadeIn(this.selfTitle.nativeElement, {
      duration: 1.2,
      y: 30,
      start: 'top 90%',
    });

    // Check screen size to determine animation behavior
    const isSmallScreen = window.innerWidth <= 768;

    if (isSmallScreen) {
      // On small screens, all elements slide in from the bottom in a stacked layout
      this.animationService.scrubSlideFromBottom(
        this.leftFeature.nativeElement,
        {
          distance: 150,
          start: 'top 80%',
          end: 'bottom 20%',
        },
      );

      this.animationService.scrubSlideFromBottom(
        this.centerFeature.nativeElement,
        {
          distance: 150,
          start: 'top 80%',
          end: 'bottom 20%',
        },
      );

      this.animationService.scrubSlideFromBottom(
        this.rightFeature.nativeElement,
        {
          distance: 150,
          start: 'top 80%',
          end: 'bottom 20%',
        },
      );
    } else {
      // On larger screens, use the original animations (left, bottom, right)
      this.animationService.scrubSlideFromLeft(this.leftFeature.nativeElement, {
        distance: 200,
        start: 'top 80%',
        end: 'bottom 20%',
      });

      this.animationService.scrubSlideFromBottom(
        this.centerFeature.nativeElement,
        {
          distance: 200,
          start: 'top 80%',
          end: 'bottom 20%',
        },
      );

      this.animationService.scrubSlideFromRight(
        this.rightFeature.nativeElement,
        {
          distance: 200,
          start: 'top 80%',
          end: 'bottom 20%',
        },
      );
    }
  }
}
