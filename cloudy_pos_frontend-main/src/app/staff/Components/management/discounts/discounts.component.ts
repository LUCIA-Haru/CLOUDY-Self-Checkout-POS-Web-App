import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import {
  Column,
  TableComponent,
} from 'app/Common/components/table/table.component';
import {
  DiscountsService,
  DiscountDTO,
  ProductDTO,
  CategoryDTO,
} from 'app/staff/service/discounts/discounts.service';

@Component({
  selector: 'app-discounts',
  standalone: true,
  imports: [CommonModule, TableComponent],
  templateUrl: './discounts.component.html',
  styleUrls: ['./discounts.component.css'],
})
export class DiscountsComponent implements OnInit {
  columns: Column[] = [
    {
      key: 'discountId',
      label: 'Discount ID',
      sortable: true,
      editable: false,
    },
    { key: 'productName', label: 'Product Name', sortable: true },
    { key: 'categoryName', label: 'Category Name', sortable: true },
    {
      key: 'isPercentage',
      label: 'Type',
      sortable: true,
      type: 'select',
      options: [
        { value: 'true', label: 'Percentage' },
        { value: 'false', label: 'Fixed Amount' },
      ],
      editable: true,
      required: true,
    },
    {
      key: 'discountValue',
      label: 'Value',
      sortable: true,
      type: 'number',
      editable: true,
      required: true,
    },
    {
      key: 'startDate',
      label: 'Start Date',
      sortable: true,
      type: 'date',
      editable: true,
      required: true,
    },
    {
      key: 'endDate',
      label: 'End Date',
      sortable: true,
      type: 'date',
      editable: true,
    },
    {
      key: 'status',
      label: 'Status',
      sortable: true,
      editable: true,
    },
    // Hidden columns for dialog only
    {
      key: 'productId',
      label: 'Product ID',
      sortable: false,
      type: 'select',
      options: [],
      editable: true,
      required: true,
    },
    {
      key: 'categoryId',
      label: 'Category ID',
      sortable: false,
      type: 'select',
      options: [],
      editable: true,
      required: true,
    },
  ];

  filterConfig = {
    column: 'status',
    options: [
      { value: 'all', label: 'All' },
      { value: 'active', label: 'Active' },
      { value: 'inactive', label: 'Inactive' },
    ],
  };

  excludedFields: string[] = [
    'discountId',
    'guid',
    'createdOn',
    'updatedOn',
    'createdBy',
    'updatedBy',
    'productName',
    'categoryName',
    'status',
  ];

  discounts: DiscountDTO[] = [];
  currentPage: number = 1;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 1;
  filterValue = '';

  products: ProductDTO[] = [];
  categories: CategoryDTO[] = [];

  constructor(
    private discountsService: DiscountsService,
    private toastr: ToastrService,
  ) {}

  ngOnInit() {
    this.fetchDiscounts(this.currentPage - 1);
    this.fetchProducts();
    this.fetchCategories();
  }

  fetchDiscounts(page: number, filterValue?: string) {
    this.discountsService
      .getDiscounts(page, this.pageSize, filterValue)
      .subscribe({
        next: (response) => {
          const today = new Date();
          this.discounts = response.content.map((discount) => {
            const startDate = new Date(discount.startDate);
            const endDate = discount.endDate
              ? new Date(discount.endDate)
              : null;
            const isActive =
              startDate <= today && (!endDate || endDate >= today);
            return {
              ...discount,
              status: isActive ? 'Active' : 'Inactive',
            };
          });

          this.totalElements = response.totalElements || 0;
          this.totalPages = response.totalPages || 1;
        },
        error: (error) => {
          this.toastr.error('Failed to fetch discounts', 'Error');
          console.error('Error fetching discounts:', error);
        },
      });
  }

  fetchProducts() {
    this.discountsService.getProducts(0, 1000).subscribe({
      next: (response) => {
        this.products = response.content;
        const productColumn = this.columns.find(
          (col) => col.key === 'productId',
        );
        if (productColumn) {
          productColumn.options = this.products.map((product) => ({
            value: product.productId.toString(),
            label: `${product.productId} - ${product.productName}`,
          }));
        }
      },
      error: (error) => {
        this.toastr.error('Failed to fetch products', 'Error');
        console.error('Error fetching products:', error);
      },
    });
  }

  fetchCategories() {
    this.discountsService.getCategories(0, 1000).subscribe({
      next: (response) => {
        this.categories = response.content;
        const categoryColumn = this.columns.find(
          (col) => col.key === 'categoryId',
        );
        if (categoryColumn) {
          categoryColumn.options = this.categories.map((category) => ({
            value: category.categoryId.toString(),
            label: `${category.categoryId} - ${category.categoryName}`,
          }));
        }
      },
      error: (error) => {
        this.toastr.error('Failed to fetch categories', 'Error');
        console.error('Error fetching categories:', error);
      },
    });
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.fetchDiscounts(this.currentPage - 1);
  }

  onRowAdd(newDiscount: any) {
    const formattedDiscount = {
      ...newDiscount,
      startDate: newDiscount.startDate
        ? new Date(newDiscount.startDate).toISOString().split('T')[0]
        : null,
      endDate: newDiscount.endDate
        ? new Date(newDiscount.endDate).toISOString().split('T')[0]
        : null,
      isPercentage:
        newDiscount.isPercentage === 'true' ||
        newDiscount.isPercentage === true,
      productId: Number(newDiscount.productId),
      categoryId: Number(newDiscount.categoryId),
    };

    this.discountsService.addDiscount(formattedDiscount).subscribe({
      next: (addedDiscount) => {
        this.toastr.success('Discount added successfully', 'Success');
        this.fetchDiscounts(this.currentPage - 1);
      },
      error: (error) => {
        this.toastr.error('Failed to add discount', 'Error');
        console.error('Error adding discount:', error);
      },
    });
  }

  onRowEdit(updatedDiscount: any) {
    const formattedDiscount = {
      ...updatedDiscount,
      startDate: updatedDiscount.startDate
        ? new Date(updatedDiscount.startDate).toISOString().split('T')[0]
        : null,
      endDate: updatedDiscount.endDate
        ? new Date(updatedDiscount.endDate).toISOString().split('T')[0]
        : null,
      isPercentage:
        updatedDiscount.isPercentage === 'true' ||
        updatedDiscount.isPercentage === true,
      productId: Number(updatedDiscount.productId),
      categoryId: Number(updatedDiscount.categoryId),
    };

    this.discountsService
      .updateDiscount(updatedDiscount.productId, formattedDiscount)
      .subscribe({
        next: (result) => {
          this.toastr.success('Discount updated successfully', 'Success');
          this.fetchDiscounts(this.currentPage - 1);
        },
        error: (error) => {
          this.toastr.error('Failed to update discount', 'Error');
          console.error('Error updating discount:', error);
        },
      });
  }

  onRowRemove(discount: DiscountDTO) {
    this.discountsService.deleteDiscount(discount.discountId).subscribe({
      next: () => {
        this.toastr.success('Discount deleted successfully', 'Success');
        this.fetchDiscounts(this.currentPage - 1);
      },
      error: (error) => {
        this.toastr.error('Failed to delete discount', 'Error');
        console.error('Error deleting discount:', error);
      },
    });
  }

  onFilterChange(filterValue: string) {
    this.filterValue = filterValue;
    this.fetchDiscounts(this.currentPage - 1);
  }
}
