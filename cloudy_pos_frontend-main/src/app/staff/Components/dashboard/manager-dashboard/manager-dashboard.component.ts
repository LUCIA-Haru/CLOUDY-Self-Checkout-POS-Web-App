import { Component, AfterViewInit, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, registerables } from 'chart.js';
import {
  CdkDragDrop,
  moveItemInArray,
  DragDropModule,
} from '@angular/cdk/drag-drop';
import { DashboardService } from 'app/staff/service/dashboard/dashboard.service';
import { AnimationService } from 'app/Common/services/animation.service';
import { Router } from '@angular/router';

Chart.register(...registerables);

@Component({
  selector: 'app-manager-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, DragDropModule],
  templateUrl: './manager-dashboard.component.html',
  styleUrls: ['./manager-dashboard.component.css'],
})
export class ManagerDashboardComponent implements AfterViewInit, OnInit {
  dashboardData: any;
  showSettingsPanel = false;
  cardVisibility = {
    revenue: true,
    customers: true,
    products: true,
    staff: true,
    charts: true,
  };
  cards = [
    {
      id: 'revenue',
      title: 'Total Revenue',
      value: '',
      class: 'text-success',
      visible: true,
    },
    {
      id: 'customers',
      title: 'Total Customers',
      value: '',
      class: 'text-primary',
      visible: true,
    },
    {
      id: 'products',
      title: 'Total Products',
      value: '',
      class: 'text-info',
      visible: true,
    },
    {
      id: 'staff',
      title: 'Total Staff',
      value: '',
      class: 'text-warning',
      visible: true,
    },
  ];

  get visibleCards() {
    return this.cards.filter((card) => card.visible);
  }
  constructor(
    private dashboardService: DashboardService,
    private animationService: AnimationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadPreferences();
    this.fetchDashboardData();
  }

  ngAfterViewInit(): void {
    this.applyAnimations();
  }

  fetchDashboardData(): void {
    this.dashboardService.fetchManagerDashboard().subscribe({
      next: (response) => {
        this.dashboardData = response.data;
        this.updateCardValues();
        this.renderCharts();
      },
      error: (error) => {
        console.error('Error fetching dashboard data:', error);
        alert(
          'Failed to load dashboard data. Please check the mock data file or try again later.',
        );
      },
    });
  }

  updateCardValues(): void {
    this.cards = this.cards.map((card) => ({
      ...card,
      value:
        card.id === 'revenue'
          ? this.dashboardData?.revenueOverTime[0]?.total
          : card.id === 'customers'
            ? this.dashboardData?.totalCustomerCount
            : card.id === 'products'
              ? this.dashboardData?.totalProductCount
              : card.id === 'staff'
                ? this.dashboardData?.totalStaffCount
                : card.value,
      visible:
        this.cardVisibility[card.id as keyof typeof this.cardVisibility] ??
        true,
    }));
  }

  applyAnimations(): void {
    // Hero Title Animation
    this.animationService.animateHeroTitle('.hero-title', { duration: 1.5 });

    // Settings Panel Animation
    if (this.showSettingsPanel) {
      this.animationService.scrollSlideInFromLeft('.settings-panel');
    }

    // Card Animations
    this.animationService.staggerScrollAppear('.card', { stagger: 0.2 });

    // Chart Animations
    if (this.cardVisibility.charts) {
      this.animationService.scrollSlideInFromLeft('.chart-card:nth-child(1)');
      this.animationService.scrollSlideInFromRight('.chart-card:nth-child(2)');
    }
  }

  renderCharts(): void {
    if (!this.cardVisibility.charts) return;

    // Revenue by Category Chart
    const revenueByCategoryCtx = document.getElementById(
      'revenueByCategoryChart',
    ) as HTMLCanvasElement;
    if (revenueByCategoryCtx) {
      new Chart(revenueByCategoryCtx, {
        type: 'bar',
        data: {
          labels: this.dashboardData.revenueByCategory.map(
            (item: any) => item.categoryName,
          ),
          datasets: [
            {
              label: 'Revenue',
              data: this.dashboardData.revenueByCategory.map(
                (item: any) => item.total,
              ),
              backgroundColor: 'rgba(40, 167, 69, 0.6)',
              borderColor: 'rgba(40, 167, 69, 1)',
              borderWidth: 1,
            },
          ],
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
              title: { display: true, text: 'Revenue ($)' },
            },
          },
        },
      });
    }

    // Top Products Chart
    const topProductsCtx = document.getElementById(
      'topProductsChart',
    ) as HTMLCanvasElement;
    if (topProductsCtx) {
      new Chart(topProductsCtx, {
        type: 'pie',
        data: {
          labels: this.dashboardData.topProducts.map(
            (item: any) => item.productName,
          ),
          datasets: [
            {
              label: 'Revenue',
              data: this.dashboardData.topProducts.map(
                (item: any) => item.totalRevenue,
              ),
              backgroundColor: [
                'rgba(220, 53, 69, 0.6)',
                'rgba(0, 123, 255, 0.6)',
                'rgba(255, 193, 7, 0.6)',
                'rgba(40, 167, 69, 0.6)',
              ],
              borderColor: [
                'rgba(220, 53, 69, 1)',
                'rgba(0, 123, 255, 1)',
                'rgba(255, 193, 7, 1)',
                'rgba(40, 167, 69, 1)',
              ],
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

  toggleSettingsPanel(): void {
    this.showSettingsPanel = !this.showSettingsPanel;
    if (this.showSettingsPanel) {
      setTimeout(
        () => this.animationService.scrollSlideInFromRight('.settings-panel'),
        0,
      );
    }
  }

  dropCard(event: CdkDragDrop<any[]>): void {
    moveItemInArray(this.cards, event.previousIndex, event.currentIndex);
    this.savePreferences();
    setTimeout(() => this.applyAnimations(), 0);
  }

  savePreferences(): void {
    this.cards = this.cards.map((card) => ({
      ...card,
      visible:
        this.cardVisibility[card.id as keyof typeof this.cardVisibility] ??
        true,
    }));
    localStorage.setItem(
      'dashboardPreferences',
      JSON.stringify({
        cardVisibility: this.cardVisibility,
        cardOrder: this.cards.map((card) => card.id),
      }),
    );
  }

  loadPreferences(): void {
    const preferences = localStorage.getItem('dashboardPreferences');
    if (preferences) {
      const parsed = JSON.parse(preferences);
      this.cardVisibility = {
        ...this.cardVisibility,
        ...parsed.cardVisibility,
      };
      const orderedCards = parsed.cardOrder
        .map(
          (id: string) =>
            this.cards.find((card) => card.id === id) ||
            this.cards.find((card) => card.id === id),
        )
        .filter(Boolean);
      this.cards = [
        ...orderedCards,
        ...this.cards.filter((card) => !parsed.cardOrder.includes(card.id)),
      ];
      this.cards = this.cards.map((card) => ({
        ...card,
        visible:
          this.cardVisibility[card.id as keyof typeof this.cardVisibility] ??
          true,
      }));
    }
  }

  resetPreferences(): void {
    this.cardVisibility = {
      revenue: true,
      customers: true,
      products: true,
      staff: true,
      charts: true,
    };
    this.cards = [
      {
        id: 'revenue',
        title: 'Total Revenue',
        value: '',
        class: 'text-success',
        visible: true,
      },
      {
        id: 'customers',
        title: 'Total Customers',
        value: '',
        class: 'text-primary',
        visible: true,
      },
      {
        id: 'products',
        title: 'Total Products',
        value: '',
        class: 'text-info',
        visible: true,
      },
      {
        id: 'staff',
        title: 'Total Staff',
        value: '',
        class: 'text-warning',
        visible: true,
      },
    ];
    localStorage.removeItem('dashboardPreferences');
    this.savePreferences();
    this.applyAnimations();
  }
  nextpage(): void {
    this.router.navigate(['/staff/secondary']);
  }
}
