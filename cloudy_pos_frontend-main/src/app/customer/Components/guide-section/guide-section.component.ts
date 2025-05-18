import { Component, ElementRef, QueryList, ViewChildren } from '@angular/core';
import { AnimationService } from 'app/Common/services/animation.service';

@Component({
  selector: 'app-guide-section',
  imports: [],
  templateUrl: './guide-section.component.html',
  styleUrl: './guide-section.component.css',
})
export class GuideSectionComponent {
  @ViewChildren('step') steps!: QueryList<ElementRef>;

  constructor(private animationService: AnimationService) {}

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.steps.forEach((step, index) => {
        this.animationService.fadeIn(step.nativeElement, 1);
      });
    }, 100);
  }
}
