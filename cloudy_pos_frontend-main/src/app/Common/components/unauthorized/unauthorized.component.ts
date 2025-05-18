import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { AnimationService } from 'app/Common/services/animation.service';

@Component({
  selector: 'app-unauthorized',
  imports: [],
  templateUrl: './unauthorized.component.html',
  styleUrl: './unauthorized.component.css',
})
export class UnauthorizedComponent implements AfterViewInit {
  @ViewChild('message') message!: ElementRef;
  @ViewChild('icon') icon!: ElementRef;
  @ViewChild('button') button!: ElementRef;

  constructor(
    private animationService: AnimationService,
    private router: Router,
  ) {}

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.animationService.fadeIn(this.message.nativeElement, 1.5);
      this.animationService.bounceIn(this.icon.nativeElement, 1.2);
      this.animationService.hoverPulse(this.button.nativeElement, {
        duration: 0.3,
        scale: 1.05,
      });
    }, 100);
  }

  goToLogin() {
    this.router.navigate(['/']);
  }
}
