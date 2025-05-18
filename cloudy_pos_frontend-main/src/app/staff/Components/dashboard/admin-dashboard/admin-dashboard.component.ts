import {
  CdkDragDrop,
  DragDropModule,
  moveItemInArray,
} from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AnimationService } from 'app/Common/services/animation.service';
import { DashboardService } from 'app/staff/service/dashboard/dashboard.service';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule, FormsModule, DragDropModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css',
})
export class AdminDashboardComponent implements OnInit, AfterViewInit {
  private dashboardService = inject(DashboardService);
  private animationService = inject(AnimationService);
  dashboardData: any;
  showSettingsPanel = false;
  cardVisibility = {
    customers: true,
    staff: true,
    products: true,
    categories: true,
    brands: true,
    trends: true,
    loyalty: true,
  };
  cards = [
    {
      id: 'customers',
      title: 'Total Customers',
      value: '',
      class: 'text-primary',
      visible: true,
    },
    {
      id: 'staff',
      title: 'Total Staff',
      value: '',
      class: 'text-warning',
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
      id: 'categories',
      title: 'Total Categories',
      value: '',
      class: 'text-success',
      visible: true,
    },
    {
      id: 'brands',
      title: 'Total Brands',
      value: '',
      class: 'text-purple',
      visible: true,
    },
  ];

  get visibleCards() {
    return this.cards.filter((card) => card.visible);
  }

  ngOnInit(): void {
    this.loadPreferences();
    this.fetchDashboardData();
  }

  ngAfterViewInit(): void {
    this.applyAnimations();
  }

  fetchDashboardData(): void {
    this.dashboardService.fetchAdminDashboard().subscribe({
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
        card.id === 'customers'
          ? this.dashboardData?.totalCustomerCount
          : card.id === 'staff'
            ? this.dashboardData?.totalStaffCount
            : card.id === 'products'
              ? this.dashboardData?.totalProductCount
              : card.id === 'categories'
                ? this.dashboardData?.totalCategoryCount
                : card.id === 'brands'
                  ? this.dashboardData?.totalBrandCount
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
      this.animationService.scrollSlideInFromRight('.settings-panel');
    }

    // Card Animations
    this.animationService.staggerScrollAppear('.card', { stagger: 0.2 });

    // Chart and Table Animations
    if (this.cardVisibility.trends) {
      this.animationService.scrollFadeIn('.chart-card');
    }
    if (this.cardVisibility.loyalty) {
      this.animationService.scrollFadeIn('.table-card');
      this.animationService.staggerScrollAppear('.table-row', { stagger: 0.1 });
    }
  }

  renderCharts(): void {
    if (!this.cardVisibility.trends) return;

    // Customer Purchase Trends Chart
    const purchaseTrendsCtx = document.getElementById(
      'purchaseTrendsChart',
    ) as HTMLCanvasElement;
    if (purchaseTrendsCtx) {
      new Chart(purchaseTrendsCtx, {
        type: 'bar',
        data: {
          labels: this.dashboardData.customerPurchaseTrends.map(
            (item: any) => item.firstName || `User ${item.userId}`,
          ),
          datasets: [
            {
              label: 'Total Spent ($)',
              data: this.dashboardData.customerPurchaseTrends.map(
                (item: any) => item.totalSpent,
              ),
              backgroundColor: 'rgba(0, 123, 255, 0.6)',
              borderColor: 'rgba(0, 123, 255, 1)',
              borderWidth: 1,
            },
          ],
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
              title: { display: true, text: 'Total Spent ($)' },
            },
            x: {
              title: { display: true, text: 'Customer' },
            },
          },
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
      'adminDashboardPreferences',
      JSON.stringify({
        cardVisibility: this.cardVisibility,
        cardOrder: this.cards.map((card) => card.id),
      }),
    );
  }

  loadPreferences(): void {
    const preferences = localStorage.getItem('adminDashboardPreferences');
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
      customers: true,
      staff: true,
      products: true,
      categories: true,
      brands: true,
      trends: true,
      loyalty: true,
    };
    this.cards = [
      {
        id: 'customers',
        title: 'Total Customers',
        value: '',
        class: 'text-primary',
        visible: true,
      },
      {
        id: 'staff',
        title: 'Total Staff',
        value: '',
        class: 'text-warning',
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
        id: 'categories',
        title: 'Total Categories',
        value: '',
        class: 'text-success',
        visible: true,
      },
      {
        id: 'brands',
        title: 'Total Brands',
        value: '',
        class: 'text-purple',
        visible: true,
      },
    ];
    localStorage.removeItem('adminDashboardPreferences');
    this.savePreferences();
    this.applyAnimations();
  }
}
