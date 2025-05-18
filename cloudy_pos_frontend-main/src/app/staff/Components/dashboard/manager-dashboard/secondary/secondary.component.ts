import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AnimationService } from 'app/Common/services/animation.service';
import { DashboardService } from 'app/staff/service/dashboard/dashboard.service';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-secondary',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './secondary.component.html',
  styleUrl: './secondary.component.css',
})
export class SecondaryComponent implements AfterViewInit {
  private dashboardService = inject(DashboardService);
  private animationService = inject(AnimationService);
  dashboardData: any;

  ngOnInit(): void {
    this.fetchDashboardData();
  }

  ngAfterViewInit(): void {
    this.applyAnimations();
  }

  fetchDashboardData(): void {
    this.dashboardService.fetchManagerDashboard().subscribe({
      next: (response: any) => {
        this.dashboardData = response.data;
        this.renderCharts();
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

    // Chart Animation
    this.animationService.scrollSlideInFromBottom('.chart-card');

    // Table Animation
    this.animationService.scrollFadeIn('.table-card');
    this.animationService.staggerScrollAppear('.table-row', { stagger: 0.1 });
  }

  renderCharts(): void {
    // Customer Demographics Chart
    const customerDemographicsCtx = document.getElementById(
      'customerDemographicsChart',
    ) as HTMLCanvasElement;
    if (customerDemographicsCtx) {
      new Chart(customerDemographicsCtx, {
        type: 'doughnut',
        data: {
          labels: this.dashboardData.customerDemographics.map(
            (item: any) => item.ageGroup,
          ),
          datasets: [
            {
              label: 'Customer Count',
              data: this.dashboardData.customerDemographics.map(
                (item: any) => item.customerCount,
              ),
              backgroundColor: [
                'rgba(111, 66, 193, 0.6)',
                'rgba(253, 126, 20, 0.6)',
              ],
              borderColor: ['rgba(111, 66, 193, 1)', 'rgba(253, 126, 20, 1)'],
              borderWidth: 1,
            },
          ],
        },
        options: {
          responsive: true,
          plugins: { legend: { position: 'top' } },
        },
      });
    }
  }
}
