import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {
  Column,
  TableComponent,
} from 'app/Common/components/table/table.component';
import { BrandService } from 'app/staff/service/brand/brand.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-brand',
  imports: [TableComponent, CommonModule],
  templateUrl: './brand.component.html',
  styleUrl: './brand.component.css',
})
export class BrandComponent implements OnInit {
  brands: any[] = [];
  currentPage: number = 1;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 1;
  columns: Column[] = [
    { key: 'id', label: 'ID', sortable: true, editable: false },
    {
      key: 'name',
      label: 'Brand Name',
      sortable: true,
      type: 'text',
      editable: true,
      required: true,
    },
    {
      key: 'isActive',
      label: 'Status',
      sortable: true,
      type: 'select',
      editable: true,
      required: true,
      options: [
        { value: 'true', label: 'Active' },
        { value: 'false', label: 'Inactive' },
      ],
    },
    { key: 'createdOn', label: 'Created On', sortable: true, editable: false },
    { key: 'createdBy', label: 'Created By', sortable: true, editable: false },
    { key: 'updatedOn', label: 'Updated On', sortable: true, editable: false },
    { key: 'updatedBy', label: 'Updated By', sortable: true, editable: false },
  ];
  filterConfig = {
    column: 'isActive',
    options: [
      { value: 'all', label: 'All' },
      { value: 'true', label: 'Active' },
      { value: 'false', label: 'Inactive' },
      // Dynamically populated below
    ],
  };

  constructor(
    private brandService: BrandService,
    private dialog: MatDialog,
    private toastr: ToastrService,
  ) {}

  ngOnInit() {
    this.fetchBrands(this.currentPage);
  }

  fetchBrands(page: number, filterValue?: string) {
    this.brandService.fetchBrands(page, this.pageSize, filterValue).subscribe({
      next: (response: any) => {
        this.brands =
          response.data.content && Array.isArray(response.data.content)
            ? response.data.content
            : [];
        this.totalElements = response.data.totalElements || 0;
        this.totalPages = response.data.totalPages || 1;
        this.updateFilterOptions();
      },
      error: (err: any) => {
        this.toastr.error('Failed to load brands');
        console.error(err);
      },
    });
  }

  updateFilterOptions() {
    const uniqueNames = [...new Set(this.brands.map((brand) => brand.name))];
    this.filterConfig.options = [
      { value: 'all', label: 'All' },
      ...uniqueNames.map((name) => ({ value: name, label: name })),
    ];
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.fetchBrands(this.currentPage - 1);
  }

  onFilterChange(filterValue: string) {
    this.currentPage = 1;
    this.fetchBrands(
      this.currentPage - 1,
      filterValue === 'all' ? undefined : filterValue,
    );
  }

  onRowAdd(brand: any) {
    this.brandService.addBrand(brand).subscribe({
      next: (response: any) => {
        this.toastr.success('Brand added successfully');
        this.fetchBrands(this.currentPage - 1);
      },
      error: (err: any) => {
        this.toastr.error('Failed to add brand');
        console.error(err);
      },
    });
  }

  onRowEdit(brand: any) {
    this.brandService.updateBrand(brand).subscribe({
      next: (response: any) => {
        this.toastr.success('Brand updated successfully');
        this.fetchBrands(this.currentPage - 1);
      },
      error: (err: any) => {
        this.toastr.error('Failed to update brand');
        console.error(err);
      },
    });
  }

  onRowRemove(brand: any) {
    this.brandService.deleteBrand(brand.id).subscribe({
      next: (response: any) => {
        this.toastr.success('Brand deleted successfully');
        this.fetchBrands(this.currentPage - 1);
      },
      error: (err: any) => {
        this.toastr.error('Failed to delete brand');
        console.error(err);
      },
    });
  }
}
