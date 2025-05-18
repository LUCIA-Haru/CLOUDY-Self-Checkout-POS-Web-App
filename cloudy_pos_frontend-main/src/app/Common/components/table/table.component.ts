import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { ToastrService } from 'ngx-toastr';
import { EditDialogComponent } from '../edit-dialog/edit-dialog.component';
import { ConfirmComponent } from 'app/Common/dialog/confirm/confirm.component';
import { LowStockProductsDialogComponent } from 'app/staff/Components/management/inventory/low-stock-products-dialog/low-stock-products-dialog.component';

export interface Column {
  key: string;
  label: string;
  sortable?: boolean;
  type?: 'text' | 'number' | 'select' | 'date';
  options?: { value: string; label: string }[];
  required?: boolean;
  editable?: boolean;
  viewLowStock?: boolean;
  hidden?: boolean; // Add hidden flag
}

interface FilterConfig {
  column: string;
  options: { value: string; label: string }[];
}

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatDatepickerModule,
    MatInputModule,
    MatNativeDateModule,
  ],
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css'],
})
export class TableComponent implements OnInit {
  @Input() columns: Column[] = [];
  @Input() data: any[] = [];
  @Input() filterConfig: FilterConfig | null = null;
  @Input() editable: boolean = false;
  @Input() pageSize: number = 10;
  @Input() currentPage: number = 1;
  @Input() totalElementsInput?: number;
  @Input() totalPagesInput?: number;
  @Input() excludedFields: string[] = [];
  @Input() entityName: string = 'Item';
  @Input() showActions: boolean = true;

  @Output() onRowAdd = new EventEmitter<any>();
  @Output() onRowEdit = new EventEmitter<any>();
  @Output() pageChange = new EventEmitter<number>();
  @Output() onRowRemove = new EventEmitter<any>();
  @Output() filterChange = new EventEmitter<string>();
  @Output() onImageManage = new EventEmitter<any>();

  filteredData: any[] = [];
  paginatedData: any[] = [];
  searchTerm: string = '';
  filterValue: string = 'all';
  sortColumn: string = '';
  sortDirection: 'asc' | 'desc' = 'asc';
  showFilterPanel: boolean = false;
  totalElements: number = 0;
  totalPages: number = 1;
  isServerSidePagination: boolean = false;

  constructor(
    private dialog: MatDialog,
    private toastrService: ToastrService,
  ) {}

  ngOnInit() {
    this.applyFilters();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (
      changes['data'] ||
      changes['totalElementsInput'] ||
      changes['totalPagesInput'] ||
      changes['currentPage']
    ) {
      this.checkPaginationType();
      this.applyFilters();
    }
  }
  private checkPaginationType() {
    // Use server-side pagination if both totalElementsInput and totalPagesInput are provided
    this.isServerSidePagination =
      this.totalElementsInput !== undefined &&
      this.totalPagesInput !== undefined;
    if (this.isServerSidePagination) {
      this.totalElements = this.totalElementsInput!;
      this.totalPages = this.totalPagesInput!;
    }
  }

  sort(key: string) {
    if (!this.columns.find((col) => col.key === key)?.sortable) return;
    this.sortDirection =
      this.sortColumn === key && this.sortDirection === 'asc' ? 'desc' : 'asc';
    this.sortColumn = key;
    this.filteredData.sort((a, b) => {
      const valueA = a[key];
      const valueB = b[key];
      if (this.sortDirection === 'asc') {
        return valueA > valueB ? 1 : valueA < valueB ? -1 : 0;
      } else {
        return valueA < valueB ? 1 : valueA > valueB ? -1 : 0;
      }
    });
    this.updatePagination();
  }

  search() {
    this.currentPage = 1;
    this.applyFilters();
  }

  filterByColumn(value: string) {
    this.filterValue = value;
    this.showFilterPanel = false;
    this.currentPage = 1;
    this.filterChange.emit(value);
    this.applyFilters();
  }

  private applyFilters() {
    // Ensure this.data is an array; if not, set to empty array
    let tempData = Array.isArray(this.data) ? [...this.data] : [];

    if (this.searchTerm) {
      const searchLower = this.searchTerm.toLowerCase();
      tempData = tempData.filter((row) =>
        this.columns.some((col) =>
          row[col.key]?.toString().toLowerCase().includes(searchLower),
        ),
      );
    }

    if (this.filterConfig && this.filterValue !== 'all') {
      const filterColumn = this.filterConfig.column;
      tempData = tempData.filter((row) => {
        const rowValue = row[filterColumn];
        if (filterColumn === 'isActive') {
          const filterVal = this.filterValue === 'true' ? true : false;
          return rowValue === filterVal;
        }
        return (
          rowValue?.toString().toLowerCase() === this.filterValue.toLowerCase()
        );
      });
    }

    this.filteredData = tempData;

    if (!this.isServerSidePagination) {
      this.totalElements = this.filteredData.length;
      this.totalPages = Math.ceil(this.totalElements / this.pageSize) || 1;
    }

    this.updatePagination();
  }

  private updatePagination() {
    if (this.isServerSidePagination) {
      this.paginatedData = this.filteredData; // Data is already paginated by the backend
    } else {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      const endIndex = startIndex + this.pageSize;
      this.paginatedData = this.filteredData.slice(startIndex, endIndex);
    }
  }

  goToPage(page: number) {
    console.log(
      'TableComponent: Going to page',
      page,
      'Total Pages:',
      this.totalPages,
    );
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    this.pageChange.emit(page);
    this.updatePagination();
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(1, this.currentPage - 2);
    let endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1);

    if (endPage - startPage + 1 < maxPagesToShow) {
      startPage = Math.max(1, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    if (pages.length === 0) {
      pages.push(1);
    }
    return pages;
  }

  openEditDialog(row: any) {
    const dialogRef = this.dialog.open(EditDialogComponent, {
      width: '600px',
      data: {
        columns: this.columns,
        row: { ...row },
        mode: 'edit',
        excludedFields: this.excludedFields,
        entityName: this.entityName,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.onRowEdit.emit(result);
      }
    });
  }

  openAddDialog() {
    const newRow: { [key: string]: any } = {};
    this.columns
      .filter((col) => col.editable !== false && !col.hidden) // Respect hidden flag
      .forEach((col) => {
        newRow[col.key] =
          col.type === 'select' ? col.options?.[0]?.value || '' : '';
      });

    const dialogRef = this.dialog.open(EditDialogComponent, {
      width: '600px',
      data: {
        columns: this.columns,
        row: newRow,
        mode: 'add',
        excludedFields: this.excludedFields,
        entityName: this.entityName,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.onRowAdd.emit(result);
      }
    });
  }

  remove(row: any) {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      width: '400px',
      data: {
        title: 'Confirm Deletion',
        message: `Are you sure you want to delete this row?`,
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.onRowRemove.emit(row);
        const index = this.filteredData.indexOf(row);
        if (index > -1) {
          this.filteredData.splice(index, 1);
          this.totalElements = this.filteredData.length;
          this.totalPages = Math.ceil(this.totalElements / this.pageSize);

          if (this.paginatedData.length === 1 && this.currentPage > 1) {
            this.currentPage--;
            this.pageChange.emit(this.currentPage);
          }

          this.updatePagination();
        }
      }
    });
  }

  hasViewLowStock(): boolean {
    return this.columns.some((col) => col.viewLowStock);
  }

  openLowStockDialog(row: any) {
    this.dialog.open(LowStockProductsDialogComponent, {
      width: '600px',
      data: { lowStockProducts: row.lowStockProductsDetails || [] },
    });
  }

  shouldShowLowStockButton(row: any): boolean {
    const hasViewLowStockColumn = this.columns.some((col) => col.viewLowStock);
    const hasLowStockProducts =
      row.lowStockProducts > 0 ||
      (row.lowStockProductsDetails && row.lowStockProductsDetails.length > 0);

    return hasViewLowStockColumn && hasLowStockProducts;
  }

  // Helper method to get the label for a select option
  getOptionLabel(
    options: { value: string; label: string }[],
    value: string,
  ): string | undefined {
    const option = options.find((opt) => opt.value === value);
    return option ? option.label : undefined;
  }

  // Helper method to get a comma-separated list of product IDs
  getProductIds(row: any): string {
    if (!row['product'] || row['product'].length === 0) {
      return 'No Products';
    }
    return row['product'].map((p: any) => p.productId).join(', ');
  }
}
