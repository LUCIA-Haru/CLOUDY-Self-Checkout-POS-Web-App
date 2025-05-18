import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { LoginModalComponent } from '../login-modal/login-modal.component';
import { MatButton } from '@angular/material/button';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from 'app/Service/auth/auth.service';
import { Router, RouterModule } from '@angular/router';
import { ConfirmComponent } from 'app/Common/dialog/confirm/confirm.component';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { AnimationService } from 'app/Common/services/animation.service';

@Component({
  selector: 'app-header',
  imports: [
    CommonModule,
    MatDialogModule,
    MatButton,
    FontAwesomeModule,
    MatIconModule,
    RouterModule,
    MatMenuModule,
    MatDividerModule,
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  standalone: true,
})
export class HeaderComponent implements OnInit, AfterViewInit, OnDestroy {
  user: any = null;
  logoPath: string = 'assets/logo.png';
  isMobile: boolean = false;
  isMenuOpen: boolean = false;
  private cleanupScroll: (() => void) | null = null;

  @ViewChild('desktopHeader', { static: false }) desktopHeader!: ElementRef;

  constructor(
    private dialog: MatDialog,
    private authService: AuthService,
    private router: Router,
    private breakpointObserver: BreakpointObserver,
    private animationService: AnimationService,
  ) {}

  ngOnInit(): void {
    // Subscribe to user data changes
    this.authService.user$.subscribe((userData) => {
      this.user = userData;
      console.log('User Data:', this.user); // Debug log to check user data
    });

    // Observe screen size changes with a custom media query
    this.breakpointObserver
      .observe(['(max-width: 798px)']) // Custom breakpoint at 798px
      .subscribe((result: BreakpointState) => {
        this.isMobile = result.matches;
        console.log(
          'Is Mobile View:',
          this.isMobile,
          'Screen Width:',
          window.innerWidth,
        );
      });
  }
  ngAfterViewInit(): void {
    if (!this.isMobile) {
      this.setupHeaderAnimation();
    }
  }

  ngOnDestroy(): void {
    // Clean up scroll animation
    if (this.cleanupScroll) {
      this.cleanupScroll();
    }
  }

  private setupHeaderAnimation(): void {
    // Initial fade-in animation
    this.animationService.fadeIn(this.desktopHeader.nativeElement, 0.5);

    // Setup scroll hide/show animation
    this.cleanupScroll = this.animationService.scrollHideShow(
      this.desktopHeader.nativeElement,
      {
        hideDistance: '-100%',
        duration: 0.5,
        scrollThreshold: 100,
        pauseDelay: 1000,
      },
      // (isHidden) => {
      //   console.log(`Header is ${isHidden ? 'hidden' : 'visible'}`);
      // },
    );
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  openLoginModel() {
    this.dialog.open(LoginModalComponent, {
      width: '50%',
      height: 'auto',
      maxHeight: '80vh',
      exitAnimationDuration: '1000ms',
      enterAnimationDuration: '1000ms',
    });
  }

  logout(): void {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      width: '350px',
      data: {
        title: 'Logout',
        message: 'Are you sure you want to log out?',
      },
    });
    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.authService.logout();
        this.router.navigate(['/home']);
      }
    });
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}
