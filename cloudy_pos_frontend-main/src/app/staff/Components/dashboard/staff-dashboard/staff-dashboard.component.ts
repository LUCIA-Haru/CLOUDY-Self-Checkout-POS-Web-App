import { Component, AfterViewInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { Chart, registerables } from 'chart.js';
import {
  CdkDragDrop,
  moveItemInArray,
  DragDropModule,
} from '@angular/cdk/drag-drop';
import { DashboardService } from 'app/staff/service/dashboard/dashboard.service';
import { AnimationService } from 'app/Common/services/animation.service';

Chart.register(...registerables);

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DragDropModule],
  templateUrl: './staff-dashboard.component.html',
  styleUrls: ['./staff-dashboard.component.css'],
})
export class StaffDashboardComponent implements AfterViewInit {
  private dashboardService = inject(DashboardService);
  private animationService = inject(AnimationService);
  private router = inject(Router);
  dashboardData: any;
  showSettingsPanel = false;
  cardVisibility = {
    products: true,
    categories: true,
    brands: true,
    customers: true,
    staff: true,
    stock: true,
  };
  cards = [
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
  ];
  private stockLevelsChartInstance: Chart | null = null; // Track chart instance

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
    this.dashboardService.fetchStaffDashboard().subscribe({
      next: (response: any) => {
        this.dashboardData = response.data;
        this.updateCardValues();
        // Delay chart rendering to ensure DOM is updated
        setTimeout(() => this.renderCharts(), 0);
      },
      error: (error: any) => {
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
        card.id === 'products'
          ? this.dashboardData?.totalProductCount
          : card.id === 'categories'
            ? this.dashboardData?.totalCategoryCount
            : card.id === 'brands'
              ? this.dashboardData?.totalBrandCount
              : card.id === 'customers'
                ? this.dashboardData?.totalCustomerCount
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
      this.animationService.scrollSlideInFromRight('.settings-panel');
    }

    // Card Animations
    this.animationService.staggerScrollAppear('.card', { stagger: 0.2 });

    // Chart Animation
    if (this.cardVisibility.stock) {
      this.animationService.scrollFadeIn('.chart-card');
    }
  }

  renderCharts(): void {
    if (!this.cardVisibility.stock) {
      // If stock card is not visible, destroy the chart if it exists
      if (this.stockLevelsChartInstance) {
        this.stockLevelsChartInstance.destroy();
        this.stockLevelsChartInstance = null;
      }
      return;
    }

    // Ensure the canvas element exists
    const stockLevelsCtx = document.getElementById(
      'stockLevelsChart',
    ) as HTMLCanvasElement | null;
    if (!stockLevelsCtx) {
      console.error('Stock Levels Chart canvas not found');
      return;
    }

    // Destroy existing chart if it exists
    if (this.stockLevelsChartInstance) {
      this.stockLevelsChartInstance.destroy();
      this.stockLevelsChartInstance = null;
    }

    // Ensure dashboardData is available
    if (!this.dashboardData?.stockLevelsByCategory) {
      console.error('Stock Levels data not available');
      return;
    }

    // Render the chart
    this.stockLevelsChartInstance = new Chart(stockLevelsCtx, {
      type: 'bar',
      data: {
        labels: this.dashboardData.stockLevelsByCategory.map(
          (item: any) => item.categoryName,
        ),
        datasets: [
          {
            label: 'Total Stock',
            data: this.dashboardData.stockLevelsByCategory.map(
              (item: any) => item.totalStock,
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
            title: { display: true, text: 'Stock Quantity' },
          },
          x: {
            title: { display: true, text: 'Category' },
          },
        },
      },
    });
  }

  nextPage(): void {
    this.router.navigate(['staff/recentPurchase']);
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
      'staffDashboardPreferences',
      JSON.stringify({
        cardVisibility: this.cardVisibility,
        cardOrder: this.cards.map((card) => card.id),
      }),
    );
    // Re-render chart after visibility changes
    this.renderCharts();
  }

  loadPreferences(): void {
    const preferences = localStorage.getItem('staffDashboardPreferences');
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
      products: true,
      categories: true,
      brands: true,
      customers: true,
      staff: true,
      stock: true,
    };
    this.cards = [
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
    ];
    localStorage.removeItem('staffDashboardPreferences');
    this.savePreferences();
    // Re-fetch data and ensure chart is re-rendered after data is available
    this.fetchDashboardData();
    // Re-apply animations after the DOM updates
    setTimeout(() => this.applyAnimations(), 0);
  }
}
