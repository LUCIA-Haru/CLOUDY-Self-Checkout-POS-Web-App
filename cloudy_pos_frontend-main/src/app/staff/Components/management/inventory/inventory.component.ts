import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { ProductDiscountReportComponent } from './product-discount-report/product-discount-report.component';
import { ExpiryProductReportComponent } from './expiry-product-report/expiry-product-report.component';
import { LowStockReportComponent } from './low-stock-report/low-stock-report.component';
import { ProductManagementComponent } from './product-management/product-management.component';

@Component({
  selector: 'app-inventory',
  imports: [
    MatTabsModule,
    CommonModule,
    ProductDiscountReportComponent,
    ExpiryProductReportComponent,
    LowStockReportComponent,
    ProductManagementComponent,
  ],
  templateUrl: './inventory.component.html',
  styleUrl: './inventory.component.css',
})
export class InventoryComponent {}
