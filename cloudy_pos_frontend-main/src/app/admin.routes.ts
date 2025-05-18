import { Routes } from '@angular/router';
import { LoginPanelComponent } from './staff/Components/LoginPanel/login-panel/login-panel.component';
import { AdminLayoutsComponent } from './staff/Components/admin-layouts/admin-layouts.component';
import { roleGuard } from './guards/role/role.guard';
import { AdminDashboardComponent } from './staff/Components/dashboard/admin-dashboard/admin-dashboard.component';
import { StaffDashboardComponent } from './staff/Components/dashboard/staff-dashboard/staff-dashboard.component';
import { ManagerDashboardComponent } from './staff/Components/dashboard/manager-dashboard/manager-dashboard.component';
import { SupplierComponent } from './staff/Components/management/supplier/supplier.component';
import { CustomerComponent } from './staff/Components/management/customer/customer.component';
import { StaffComponent } from './staff/Components/management/staff/staff.component';
import { DiscountsComponent } from './staff/Components/management/discounts/discounts.component';
import { CouponsComponent } from './staff/Components/management/coupons/coupons.component';
import { AnalyticsComponent } from './staff/Components/analytics/analytics/analytics.component';
import { SecondaryComponent } from './staff/Components/dashboard/manager-dashboard/secondary/secondary.component';
import { SecondComponent } from './staff/Components/dashboard/staff-dashboard/second/second.component';
import { ThirdComponent } from './staff/Components/dashboard/staff-dashboard/third/third.component';
import { CategoriesComponent } from './staff/Components/managment/categories/categories.component';
import { BrandComponent } from './staff/Components/managment/brand/brand.component';
import { InventoryComponent } from './staff/Components/management/inventory/inventory.component';
import { SupplierPurchaseComponent } from './staff/Components/management/supplier-purchase/supplier-purchase.component';
import { SharedStaffProfileComponent } from './Common/components/shared-staff-profile/shared-staff-profile.component';

export const adminRoutes: Routes = [
  // Login route without sidebar
  {
    path: 'loginPanel',
    component: LoginPanelComponent,
  },
  // Routes with sidebar (under AdminLayoutsComponent)
  {
    path: '',
    component: AdminLayoutsComponent,
    children: [
      // Remove default redirect to avoid forcing a dashboard
      { path: '', redirectTo: 'loginPanel', pathMatch: 'full' }, // Redirect /staff to loginPanel
      // Admin Dashboard
      {
        path: 'admin-dashboard',
        component: AdminDashboardComponent,
        canActivate: [roleGuard],
        data: { role: ['ADMIN'] },
      },
      // Staff Dashboard
      {
        path: 'staff-dashboard',
        component: StaffDashboardComponent,
        canActivate: [roleGuard],
        data: { role: ['STAFF'] },
      },
      {
        path: 'recentPurchase',
        component: SecondComponent,
        canActivate: [roleGuard],
        data: { role: ['STAFF'] },
      },
      {
        path: 'recentpurchasetwo',
        component: ThirdComponent,
        canActivate: [roleGuard],
        data: { role: ['STAFF'] },
      },

      {
        path: 'inventory',
        component: InventoryComponent,
        canActivate: [roleGuard],
        data: { role: ['STAFF'] },
      },
      {
        path: 'brand',
        component: BrandComponent,
        canActivate: [roleGuard],
        data: { role: ['STAFF'] },
      },
      {
        path: 'categories',
        component: CategoriesComponent,
        canActivate: [roleGuard],
        data: { role: ['STAFF'] },
      },
      // Manager Dashboard
      {
        path: 'manager-dashboard',
        component: ManagerDashboardComponent,
        canActivate: [roleGuard],
        data: { role: ['MANAGER'] },
      },
      {
        path: 'secondary',
        component: SecondaryComponent,
        canActivate: [roleGuard],
        data: { role: ['MANAGER'] },
      },

      // Supplier Route
      {
        path: 'supplier',
        component: SupplierComponent,
        canActivate: [roleGuard],
        data: { role: ['MANAGER'] },
      },
      {
        path: 'purchase',
        component: SupplierPurchaseComponent,
        canActivate: [roleGuard],
        data: { role: ['MANAGER'] },
      },
      {
        path: 'customer',
        component: CustomerComponent,
        canActivate: [roleGuard],
        data: { role: ['MANAGER', 'ADMIN', 'STAFF'] },
      },
      {
        path: 'manage',
        component: StaffComponent,
        canActivate: [roleGuard],
        data: { role: ['MANAGER', 'ADMIN'] },
      },
      {
        path: 'discounts',
        component: DiscountsComponent,
        canActivate: [roleGuard],
        data: { role: ['MANAGER'] },
      },
      {
        path: 'analytics',
        component: AnalyticsComponent,
        canActivate: [roleGuard],
        data: { role: ['MANAGER'] },
      },
      {
        path: 'coupons',
        component: CouponsComponent,
        canActivate: [roleGuard],
        data: { role: ['ADMIN'] },
      },
      {
        path: 'profile',
        component: SharedStaffProfileComponent,
        canActivate: [roleGuard],
        data: { role: ['ADMIN', 'MANAGER', 'STAFF'] },
      },
      // Remove generic dashboard redirect to avoid conflicts
      // { path: 'dashboard', redirectTo: 'admin-dashboard', pathMatch: 'full' },
    ],
  },
];
