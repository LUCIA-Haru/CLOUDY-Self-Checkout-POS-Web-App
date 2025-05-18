import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog'; // Import MatDialog and MatDialogModule
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from 'app/Service/auth/auth.service';

import { filter } from 'rxjs/operators';
import { ConfirmComponent } from 'app/Common/dialog/confirm/confirm.component';

interface NavItem {
  name: string;
  icon: string;
  route?: string;
  children?: NavItem[];
  expanded?: boolean;
}

@Component({
  imports: [CommonModule, MatIconModule, MatDialogModule], // Add MatDialogModule to imports
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent implements OnInit {
  tabs: NavItem[] = [];
  userRole!: string;
  activeIndex = 0;
  activeChildIndex: number | null = null;

  constructor(
    private router: Router,
    private authService: AuthService,
    private dialog: MatDialog, // Inject MatDialog
  ) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.updateActiveTab();
      });
  }

  ngOnInit(): void {
    this.userRole = this.authService.getRoles();
    this.setNavItemsBasedOnRole(this.userRole);
    this.updateActiveTab();
  }

  setActive(parentIndex: number, childIndex?: number): void {
    const tab = this.tabs[parentIndex];

    if (childIndex !== undefined && tab.children) {
      this.activeIndex = parentIndex;
      this.activeChildIndex = childIndex;
      const childTab = tab.children[childIndex];
      if (childTab.route) {
        this.router.navigate([childTab.route]);
      }
      tab.expanded = true;
      return;
    }

    this.activeIndex = parentIndex;
    this.activeChildIndex = null;

    if (tab.children) {
      this.toggleExpand(parentIndex);
      return;
    }

    if (tab.route) {
      this.router.navigate([tab.route]);
    }
  }

  toggleExpand(index: number): void {
    const tab = this.tabs[index];
    if (tab.children) {
      tab.expanded = !tab.expanded;
    }
  }

  get tabCount(): number {
    return this.tabs.length;
  }

  private setNavItemsBasedOnRole(role: string | null): void {
    switch (role) {
      case 'MANAGER':
        this.tabs = [
          {
            name: 'Dashboard',
            icon: 'dashboard',
            route: '/staff/manager-dashboard',
          },
          {
            name: 'Customers',
            icon: 'people',
            route: '/staff/customer',
          },
          {
            name: 'Supplier',
            icon: 'local_shipping',
            route: '/staff/supplier',
          },
          {
            name: 'Purchase',
            icon: 'storefront',
            route: '/staff/purchase',
          },
          {
            name: 'Staff',
            icon: 'people',
            route: '/staff/manage',
          },
          // {
          //   name: 'Analytics',
          //   icon: 'analytics',
          //   route: '/staff/analytics',
          // },
          {
            name: 'Discounts',
            icon: 'sell',
            route: '/staff/discounts',
          },
          {
            name: 'Profile',
            icon: 'person',
            route: '/staff/profile',
          },

          // {
          //   name: 'Notifications',
          //   icon: 'notifications_active',
          //   route: '/staff/notifications',
          // },
          {
            name: 'Logout',
            icon: 'exit_to_app',
            route: '', // Remove the route since we'll handle logout via a method
          },
        ];
        break;

      case 'ADMIN':
        this.tabs = [
          {
            name: 'Dashboard',
            icon: 'dashboard',
            route: '/staff/admin-dashboard',
          },
          { name: 'Customers', icon: 'people', route: '/staff/customer' },
          {
            name: 'Staff',
            icon: 'people',
            route: '/staff/manage',
          },
          // {
          //   name: 'Loyalty Program',
          //   icon: 'card_giftcard',
          //   route: '/staff/loyalty',
          // },
          {
            name: 'Coupons',
            icon: 'local_offer',
            route: '/staff/coupons',
          },
          {
            name: 'Profile',
            icon: 'person',
            route: '/staff/profile',
          },
          {
            name: 'Logout',
            icon: 'exit_to_app',
            route: '', // Remove the route since we'll handle logout via a method
          },
        ];
        break;

      case 'STAFF':
        this.tabs = [
          {
            name: 'Dashboard',
            icon: 'dashboard',
            route: '/staff/staff-dashboard',
          },
          {
            name: 'Category',
            icon: 'category',
            route: '/staff/categories',
          },
          {
            name: 'Products',
            icon: 'inventory_2',
            route: '/staff/inventory',
          },
          {
            name: 'Brands',
            icon: 'store_mall_directory',
            route: '/staff/brand',
          },
          {
            name: 'Profile',
            icon: 'person',
            route: '/staff/profile',
          },
          // {
          //   name: 'Notifications',
          //   icon: 'notifications_active',
          //   route: '/staff/notifications',
          // },
          {
            name: 'Logout',
            icon: 'exit_to_app',
            route: '', // Remove the route since we'll handle logout via a method
          },
        ];
        break;

      default:
        this.tabs = [];
        break;
    }
  }

  private updateActiveTab(): void {
    const currentRoute = this.router.url;

    this.tabs.forEach((tab, parentIndex) => {
      if (tab.route === currentRoute) {
        this.activeIndex = parentIndex;
        this.activeChildIndex = null;
        return;
      }
      if (tab.children) {
        const childIndex = tab.children.findIndex(
          (child) => child.route === currentRoute,
        );
        if (childIndex !== -1) {
          this.activeIndex = parentIndex;
          this.activeChildIndex = childIndex;
          tab.expanded = true;
        }
      }
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
        this.router.navigate(['/staff/loginPanel']);
      }
    });
  }
}
