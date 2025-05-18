import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { CustomerDataComponent } from '../customer-data/customer-data.component';
import { SalesDataComponent } from '../sales-data/sales-data.component';
import { StaffDataComponent } from '../staff-data/staff-data.component';
import { SupplierDataComponent } from '../supplier-data/supplier-data.component';

@Component({
  selector: 'app-analytics',
  imports: [
    CommonModule,
    MatTabsModule,
    CustomerDataComponent,
    SalesDataComponent,
    StaffDataComponent,
    SupplierDataComponent,
  ],
  templateUrl: './analytics.component.html',
  styleUrl: './analytics.component.css',
})
export class AnalyticsComponent {}
