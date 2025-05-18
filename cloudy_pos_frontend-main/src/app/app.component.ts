import {
  AfterViewInit,
  Component,
  ElementRef,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { HeaderComponent } from './customer/Components/header/header.component';
import { FooterComponent } from './customer/Components/footer/footer.component';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { AnimationService } from './Common/services/animation.service';
import { PreLoaderComponent } from './customer/Components/pre-loader/pre-loader.component';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    HeaderComponent,
    FooterComponent,
    MatButtonModule,
    CommonModule,
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class AppComponent implements AfterViewInit {
  // title = 'Cloudy_SelfCheckout_Angular';
  constructor(
    private router: Router,
    private animationService: AnimationService,
  ) {}
  isCustomerPage(): boolean {
    return (
      this.router.url.startsWith('/') && !this.router.url.startsWith('/staff')
    );
  }

  isStaffPage(): boolean {
    return this.router.url.startsWith('/staff');
  }

  ngAfterViewInit(): void {
    // Apply animateHeroTitle to all elements with the class 'title'
    const titles = document.querySelectorAll('.title');
    if (titles.length > 0) {
      titles.forEach((title) => {
        this.animationService.animateHeroTitle(title as HTMLElement);
      });
    }

    // Optional: Observe DOM changes to apply animation to dynamically added titles
    const observer = new MutationObserver((mutations) => {
      mutations.forEach((mutation) => {
        const newTitles = (mutation.target as Element).querySelectorAll(
          '.title',
        );
        newTitles.forEach((title) => {
          if (!title.hasAttribute('data-animated')) {
            this.animationService.animateHeroTitle(title as HTMLElement);
            title.setAttribute('data-animated', 'true');
          }
        });
      });
    });

    observer.observe(document.body, {
      childList: true,
      subtree: true,
    });
  }
}
