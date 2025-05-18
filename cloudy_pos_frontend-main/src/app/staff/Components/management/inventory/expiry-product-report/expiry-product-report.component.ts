import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Column,
  TableComponent,
} from 'app/Common/components/table/table.component';
import { ToastService } from 'app/Service/toast/toast.service';
import { InventoryService } from 'app/staff/service/inventory/inventory.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-expiry-product-report',
  imports: [TableComponent, CommonModule, FormsModule],
  templateUrl: './expiry-product-report.component.html',
  styleUrl: './expiry-product-report.component.css',
})
export class ExpiryProductReportComponent implements OnInit {
  products: any[] = [];
  currentPage: number = 1;
  pageSize: number = 10;
  totalElements: number = 0;
  daysThreshold: number = 30; // Default value
  columns: Column[] = [
    { key: 'productId', label: 'ID', sortable: true },
    { key: 'productName', label: 'Product Name', sortable: true },
    { key: 'barcode', label: 'Barcode', sortable: true },
    { key: 'expiryDate', label: 'Expiry Date', sortable: true },
    { key: 'stockUnit', label: 'Stock', sortable: true },
  ];
  filterConfig = {
    column: 'productName',
    options: [
      { value: 'all', label: 'All' },
      // Dynamically populated
    ],
  };

  constructor(
    private inventoryService: InventoryService,
    private toastr: ToastrService,
  ) {}

  ngOnInit() {
    this.fetchProductExpiryAlerts();
  }

  fetchProductExpiryAlerts(filterValue?: string) {
    this.inventoryService.fetchExpiryProducts(this.daysThreshold).subscribe({
      next: (response: any) => {
        let products = response.data.content;
        if (filterValue && filterValue !== 'all') {
          products = products.filter((product: any) =>
            product.productName
              .toLowerCase()
              .includes(filterValue.toLowerCase()),
          );
        }
        this.products = products;
        this.totalElements = response.data.total;
        this.updateFilterOptions();
        this.updatePagination();
      },
      error: (err: any) => {
        this.toastr.error('Failed to load product expiry alerts');
        console.error(err);
      },
    });
  }

  updateFilterOptions() {
    const uniqueNames = [
      ...new Set(this.products.map((product) => product.productName)),
    ];
    this.filterConfig.options = [
      { value: 'all', label: 'All' },
      ...uniqueNames.map((name) => ({ value: name, label: name })),
    ];
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.updatePagination();
  }

  onFilterChange(filterValue: string) {
    this.currentPage = 1;
    this.fetchProductExpiryAlerts(filterValue);
  }

  updatePagination() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.products = this.products.slice(startIndex, endIndex);
  }

  onThresholdChange() {
    if (this.daysThreshold >= 0) {
      this.currentPage = 1;
      this.fetchProductExpiryAlerts();
    } else {
      this.toastr.error('Days threshold must be non-negative');
    }
  }
}
