import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  Column,
  TableComponent,
} from 'app/Common/components/table/table.component';
import { ToastService } from 'app/Service/toast/toast.service';
import { Supplier } from 'app/staff/model/Supplier';
import {
  SupplierTransactionRequest,
  SupplierTransactions,
} from 'app/staff/model/SupplierTransaction';
import { BrandService } from 'app/staff/service/brand/brand.service';
import { CategoriesService } from 'app/staff/service/categories/categories.service';
import { CategoryDTO } from 'app/staff/service/discounts/discounts.service';
import { InventoryService } from 'app/staff/service/inventory/inventory.service';
import { SupplierService } from 'app/staff/service/supplier/supplier.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-supplier-purchase',
  imports: [TableComponent, CommonModule],
  templateUrl: './supplier-purchase.component.html',
  styleUrls: ['./supplier-purchase.component.css'],
  standalone: true,
})
export class SupplierPurchaseComponent implements OnInit, OnDestroy {
  allTransactions: SupplierTransactions[] = [];
  products: any[] = [];
  categories: CategoryDTO[] = [];
  brands: any[] = [];
  suppliers: Supplier[] = [];

  private subscriptions: Subscription = new Subscription();

  pageSize = 10;
  currentPage = 1;
  totalElements = 0;
  totalPages = 1;
  filterValue = '';

  columns: Column[] = [
    { key: 'transactionId', label: 'ID', sortable: true, editable: false },
    {
      key: 'supplierId',
      label: 'SupplierID',
      sortable: true,
      type: 'select',
      options: [],
    },
    {
      key: 'categoryId',
      label: 'CategoryID',
      sortable: true,
      type: 'select',
      options: [],
    },
    {
      key: 'brandId',
      label: 'BrandID',
      sortable: true,
      type: 'select',
      options: [],
    },
    {
      key: 'products',
      label: 'ProductID',
      sortable: false,
      type: 'text',
      editable: false,
    },
    {
      key: 'transactionDate',
      label: 'TransactionDate',
      sortable: true,
      editable: false,
      type: 'date',
    },
    {
      key: 'quantity',
      label: 'Quantity',
      sortable: true,
      type: 'number',
    },
    { key: 'createdBy', label: 'CreatedBy', sortable: false, editable: false },
    // Hidden columns for add dialog only
    {
      key: 'productId',
      label: 'ProductID',
      type: 'select',
      options: [],
      required: true,
      editable: true,
      hidden: true, // Add hidden flag
    },
    {
      key: 'manuDate',
      label: 'Manufacture Date',
      type: 'date',
      required: true,
      editable: true,
      hidden: true, // Add hidden flag
    },
    {
      key: 'expDate',
      label: 'Expire Date',
      type: 'date',
      required: true,
      editable: true,
      hidden: true, // Add hidden flag
    },
  ];

  constructor(
    private toastr: ToastService,
    private brandServcie: BrandService,
    private categoryService: CategoriesService,
    private productSerevice: InventoryService,
    private purhcaseService: SupplierService,
  ) {}

  ngOnInit(): void {
    this.fetchTransactions();
    this.fetchBrands();
    this.fetchCategories();
    this.fetchProducts();
    this.fetchSuppliers();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  private fetchTransactions(): void {
    const sub = this.purhcaseService.getAllTransaction().subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.allTransactions = response.data;
          this.totalElements = this.allTransactions.length;
          this.totalPages = Math.ceil(this.totalElements / this.pageSize);
        } else {
          this.toastr.showError('Failed to fetch transactions');
        }
      },
      error: (error) => {
        console.error('Error fetching transactions:', error);
        this.toastr.showError('Error fetching transactions');
      },
    });
    this.subscriptions.add(sub);
  }

  private fetchCategories(): void {
    const sub = this.categoryService.fetchCategories(0, 100).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.categories = response.data.content;
          const categoryColumn = this.columns.find(
            (col) => col.key === 'categoryId',
          );
          if (categoryColumn) {
            categoryColumn.options = this.categories.map((category) => ({
              value: category.categoryId.toString(),
              label: `${category.categoryId} - ${category.categoryName}`,
            }));
          }
        }
      },
      error: (error) => {
        this.toastr.showError('Failed to fetch categories');
        console.error('Error fetching categories:', error);
      },
    });
    this.subscriptions.add(sub);
  }

  private fetchBrands(): void {
    const sub = this.brandServcie.fetchBrands(0, 100).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.brands = response.data.content;
          const brandColumn = this.columns.find((col) => col.key === 'brandId');
          if (brandColumn) {
            brandColumn.options = this.brands.map((brand) => ({
              value: brand.id.toString(),
              label: `${brand.id} - ${brand.name}`,
            }));
          }
        }
      },
      error: (error) => {
        this.toastr.showError('Failed to fetch brands');
        console.error('Error fetching brands:', error);
      },
    });
    this.subscriptions.add(sub);
  }

  private fetchProducts(
    filterValue: string = 'all',
    page: number = 0,
    size: number = this.pageSize,
  ): void {
    const sub = this.productSerevice
      .getAllProducts(page, size, filterValue)
      .subscribe({
        next: (response) => {
          if (response.status === 'success') {
            this.products = response.data.content;
            const productColumn = this.columns.find(
              (col) => col.key === 'productId',
            );
            if (productColumn) {
              productColumn.options = this.products.map((product) => ({
                value: product.productId.toString(),
                label: `${product.productId} - ${product.productName}`,
              }));
            }
          }
        },
        error: (error) => {
          this.toastr.showError('Failed to fetch products');
          console.error('Error fetching products', error);
        },
      });
    this.subscriptions.add(sub);
  }

  private fetchSuppliers(page: number = 0, size: number = this.pageSize): void {
    const sub = this.purhcaseService.getSuppliers(page, size).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.suppliers = response.data.content;
          const suppliersColumn = this.columns.find(
            (col) => col.key === 'supplierId',
          );
          if (suppliersColumn) {
            suppliersColumn.options = this.suppliers.map((supplier) => ({
              value: supplier.id.toString(),
              label: `${supplier.id} - ${supplier.name}`,
            }));
          }
        }
      },
      error: (error) => {
        this.toastr.showError('Failed to fetch suppliers');
        console.error('Error fetching suppliers', error);
      },
    });
    this.subscriptions.add(sub);
  }

  onRowAdd(newTransaction: SupplierTransactionRequest): void {
    const sub = this.purhcaseService.addTransaction(newTransaction).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.toastr.showSuccess('New Transaction was added');
          this.fetchTransactions();
        } else {
          this.toastr.showError('Failed to add transaction');
        }
      },
      error: (error) => {
        this.toastr.showError('Failed to add transaction');
        console.error('Error adding transaction', error);
      },
    });
    this.subscriptions.add(sub);
  }

  onPageChange(page: number) {
    this.currentPage = page;
    // TableComponent already handles pagination
  }

  onFilterChange(filterValue: string) {
    this.filterValue = filterValue;
    this.currentPage = 1;
    this.fetchTransactions();
  }
}
