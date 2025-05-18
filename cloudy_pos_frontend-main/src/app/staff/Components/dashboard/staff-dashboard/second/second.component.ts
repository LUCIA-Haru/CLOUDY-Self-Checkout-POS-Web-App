import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AnimationService } from 'app/Common/services/animation.service';
import { DashboardService } from 'app/staff/service/dashboard/dashboard.service';

@Component({
  selector: 'app-second',
  imports: [CommonModule],
  templateUrl: './second.component.html',
  styleUrl: './second.component.css',
})
export class SecondComponent {
  private dashboardService = inject(DashboardService);
  private animationService = inject(AnimationService);
  private router = inject(Router);
  recentPurchases: any[] = [];

  ngOnInit(): void {
    this.fetchDashboardData();
  }

  ngAfterViewInit(): void {
    this.applyAnimations();
  }

  fetchDashboardData(): void {
    this.dashboardService.fetchStaffDashboard().subscribe({
      next: (response: any) => {
        this.recentPurchases = response.data.recentPurchases.slice(0, 20); // First 20 entries
      },
      error: (error: any) => {
        console.error('Error fetching dashboard data:', error);
        alert(
          'Failed to load dashboard data. Please check the mock data file or try again later.',
        );
      },
    });
  }

  applyAnimations(): void {
    // Hero Title Animation
    this.animationService.animateHeroTitle('.hero-title', { duration: 1.5 });

    // Table Animation
    this.animationService.scrollFadeIn('.table-card');
    this.animationService.staggerScrollAppear('.table-row', { stagger: 0.1 });
  }

  previousPage(): void {
    this.router.navigate(['/staff/staff-dashboard']);
  }

  nextPage(): void {
    this.router.navigate(['/staff/recentpurchasetwo']);
  }
}
