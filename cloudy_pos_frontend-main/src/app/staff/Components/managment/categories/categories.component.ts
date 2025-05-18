import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {
  Column,
  TableComponent,
} from 'app/Common/components/table/table.component';
import { CategoriesService } from 'app/staff/service/categories/categories.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-categories',
  imports: [CommonModule, TableComponent],
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css'], // Fixed typo: styleUrl -> styleUrls
  standalone: true, // Added since TableComponent is standalone
})
export class CategoriesComponent {
  categories: any[] = [];
  currentPage: number = 1;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 1;
  columns: Column[] = [
    { key: 'categoryId', label: 'ID', sortable: true, editable: false },
    {
      key: 'categoryName',
      label: 'Category Name',
      sortable: true,
      type: 'text',
      editable: true,
      required: true,
    },
    {
      key: 'categoryDesc',
      label: 'Description',
      sortable: true,
      type: 'text',
      editable: true,
    },
    {
      key: 'aisle',
      label: 'Aisle',
      sortable: true,
      type: 'number',
      editable: true,
      required: true,
    },
  ];
  filterConfig = {
    column: 'categoryName',
    options: [
      { value: 'all', label: 'All' },
      // Dynamically populated below
    ],
  };

  constructor(
    private categoriesService: CategoriesService,
    private dialog: MatDialog,
    private toastr: ToastrService,
  ) {}

  ngOnInit() {
    this.fetchCategories(this.currentPage - 1);
  }

  fetchCategories(page: number, filterValue?: string) {
    console.log(
      'CategoriesComponent: Fetching page',
      page,
      'Filter:',
      filterValue,
    );
    this.categoriesService
      .fetchCategories(page, this.pageSize, filterValue)
      .subscribe({
        next: (response) => {
          const data = response.data || {};
          this.categories =
            data.content && Array.isArray(data.content) ? data.content : [];
          this.totalElements = data.totalElements || 0;
          this.totalPages = data.totalPages || 1;

          this.updateFilterOptions();
        },
        error: (err) => {
          this.toastr.error('Failed to load categories');
          console.error(err);
        },
      });
  }

  updateFilterOptions() {
    // Ensure this.categories is an array before mapping
    const uniqueNames = Array.isArray(this.categories)
      ? [
          ...new Set(
            this.categories
              .map((cat) => cat.categoryName)
              .filter((name) => name !== undefined && name !== null),
          ),
        ]
      : [];
    this.filterConfig.options = [
      { value: 'all', label: 'All' },
      ...uniqueNames.map((name) => ({ value: name, label: name })),
    ];
  }

  onPageChange(page: number) {
    console.log('CategoriesComponent: Page change to', page);
    this.currentPage = page;
    this.fetchCategories(this.currentPage - 1);
  }

  onFilterChange(filterValue: string) {
    this.currentPage = 1;
    // Fixed: Removed undefined 'page' variable, use currentPage instead
    this.fetchCategories(
      this.currentPage - 1,
      filterValue === 'all' ? undefined : filterValue,
    );
  }

  onRowAdd(category: any) {
    this.categoriesService.addCategory(category).subscribe({
      next: (response) => {
        this.toastr.success('Category added successfully');
        this.fetchCategories(this.currentPage - 1); // Refetch current page
      },
      error: (err) => {
        this.toastr.error('Failed to add category');
        console.error(err);
      },
    });
  }

  onRowEdit(category: any) {
    this.categoriesService
      .updateCategory(category.categoryId, category)
      .subscribe({
        next: (response) => {
          this.toastr.success('Category updated successfully');
          this.fetchCategories(this.currentPage - 1); // Refetch current page
        },
        error: (err) => {
          this.toastr.error('Failed to update category');
          console.error(err);
        },
      });
  }

  onRowRemove(category: any) {
    this.categoriesService.deleteCategory(category.categoryId).subscribe({
      next: (response) => {
        this.toastr.success('Category deleted successfully');
        this.fetchCategories(this.currentPage - 1); // Refetch current page
      },
      error: (err) => {
        this.toastr.error('Failed to delete category');
        console.error(err);
      },
    });
  }
}
