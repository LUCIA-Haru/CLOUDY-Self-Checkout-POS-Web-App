import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { InventoryService } from 'app/staff/service/inventory/inventory.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  TableComponent,
  Column,
} from 'app/Common/components/table/table.component';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-low-stock-report',
  standalone: true,
  imports: [CommonModule, FormsModule, TableComponent, MatDialogModule],
  templateUrl: './low-stock-report.component.html',
  styleUrls: ['./low-stock-report.component.css'],
})
export class LowStockReportComponent implements OnInit {
  stockLevels: any[] = [];
  currentPage: number = 1;
  pageSize: number = 10;
  totalElements: number = 0;
  columns: Column[] = [
    {
      key: 'categoryId',
      label: 'Category Id',
      sortable: true,
      editable: false,
    },
    {
      key: 'categoryName',
      label: 'Category Name',
      sortable: true,
      editable: false,
    },
    {
      key: 'totalProducts',
      label: 'Total Products',
      sortable: true,
      editable: false,
    },
    {
      key: 'totalStockUnits',
      label: 'Total Stock Units',
      sortable: true,
      editable: false,
    },
    {
      key: 'lowStockProducts',
      label: 'Low Stock Products',
      sortable: true,
      viewLowStock: true,
    },
  ];
  filterConfig = {
    column: 'categoryName',
    options: [
      { value: 'all', label: 'All' },
      // Dynamically populated
    ],
  };

  constructor(
    private stockLevelsByCategoryService: InventoryService,
    private toastr: ToastrService,
  ) {}

  ngOnInit() {
    this.fetchStockLevels();
  }

  fetchStockLevels(filterValue?: string) {
    this.stockLevelsByCategoryService.fetchStockLevelsByCategory().subscribe({
      next: (response: any) => {
        let stockLevels = response.data.content;
        if (filterValue && filterValue !== 'all') {
          stockLevels = stockLevels.filter((stock: any) =>
            stock.categoryName
              .toLowerCase()
              .includes(filterValue.toLowerCase()),
          );
        }
        this.stockLevels = stockLevels;
        this.totalElements = response.data.total;
        this.updateFilterOptions();
        this.updatePagination();
      },
      error: (err: any) => {
        this.toastr.error('Failed to load stock levels by category');
        console.error(err);
      },
    });
  }

  updateFilterOptions() {
    const uniqueNames = [
      ...new Set(this.stockLevels.map((stock) => stock.categoryName)),
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
    this.fetchStockLevels(filterValue);
  }

  updatePagination() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.stockLevels = this.stockLevels.slice(startIndex, endIndex);
  }
}
