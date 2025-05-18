import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { AnimationService } from 'app/Common/services/animation.service';

@Component({
  selector: 'app-pre-loader',
  imports: [],
  templateUrl: './pre-loader.component.html',
  styleUrl: './pre-loader.component.css',
})
export class PreLoaderComponent implements AfterViewInit {
  @ViewChild('preloader') preloader!: ElementRef;

  constructor(private animationService: AnimationService) {}

  ngAfterViewInit(): void {
    // Delay to ensure DOM is ready
    setTimeout(() => {
      this.animationService.preloaderReveal(
        this.preloader.nativeElement,
        this.preloader.nativeElement.querySelector('.preloader-text'),
        () => {
          // Hide preloader after animation
          const preloader = document.querySelector('#preloader') as HTMLElement;
          if (preloader) {
            preloader.style.display = 'none';
          }
        },
        {
          duration: 2.5,
          delay: 0.5,
        },
      );
    }, 100);
  }
}
