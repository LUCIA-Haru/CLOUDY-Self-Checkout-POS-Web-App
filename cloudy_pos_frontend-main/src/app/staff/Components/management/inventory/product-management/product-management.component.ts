import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  Column,
  TableComponent,
} from 'app/Common/components/table/table.component';
import { InventoryService } from 'app/staff/service/inventory/inventory.service';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { ImageUploadDialogComponentComponent } from '../image-upload-dialog-component/image-upload-dialog-component.component';
import { CategoriesService } from 'app/staff/service/categories/categories.service';
import { BrandService } from 'app/staff/service/brand/brand.service';
import { CategoryDTO } from 'app/staff/service/discounts/discounts.service';
import { ToastService } from 'app/Service/toast/toast.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-product-management',
  standalone: true,
  imports: [CommonModule, TableComponent],
  templateUrl: './product-management.component.html',
  styleUrls: ['./product-management.component.css'],
})
export class ProductManagementComponent implements OnInit {
  categories: CategoryDTO[] = [];
  brands: any[] = [];
  columns: Column[] = [
    {
      key: 'productName',
      label: 'Product Name',
      sortable: true,
      type: 'text',
      required: true,
      editable: true,
    },
    {
      key: 'category',
      label: 'Category',
      sortable: true,
      type: 'select',
      options: [],
      editable: true,
    },
    {
      key: 'brand',
      label: 'Brand',
      sortable: true,
      type: 'select',
      options: [],
      editable: true,
    },
    // {
    //   key: 'transaction',
    //   label: 'Transaction',
    //   sortable: true,
    //   type: 'select',
    //   options: [],
    //   editable: true,
    // },
    {
      key: 'price',
      label: 'Price $',
      sortable: true,
      type: 'number',
      editable: true,
    },
    {
      key: 'stockUnit',
      label: 'Stock',
      sortable: true,
      type: 'number',
      editable: true,
      viewLowStock: false,
    },
    {
      key: 'rating',
      label: 'Rating',
      sortable: true,
      type: 'number',
      editable: true,
      viewLowStock: false,
    },
    {
      key: 'manuDate',
      label: 'Manufacture Date',
      sortable: true,
      type: 'date',
      editable: true,
    },
    {
      key: 'expDate',
      label: 'Expiry Date',
      sortable: true,
      type: 'date',
      editable: true,
    },
    {
      key: 'productDesc',
      label: 'Description',
      sortable: false,
      type: 'text',
      editable: true,
    },
    // {
    //   key: 'hasDiscount',
    //   label: 'Discount',
    //   sortable: true,
    //   type: 'select',
    //   options: [
    //     { value: 'true', label: 'Yes' },
    //     { value: 'false', label: 'No' },
    //   ],
    //   editable: true,
    // },
    { key: 'imgUrls', label: 'Images', type: 'text', editable: false },
  ];

  products: any[] = [];
  filterConfig = {
    column: 'hasDiscount',
    options: [
      { value: 'all', label: 'All' },
      { value: 'true', label: 'Discounted' },
      { value: 'false', label: 'Non-Discounted' },
    ],
  };
  currentPage: number = 1;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 1;
  editable = true;
  excludedFields = [
    'productólogos',
    'productGuid',
    'createdOn',
    'createdBy',
    'updatedOn',
    'updatedBy',
    'barcode',
    'currency',
    'transactionId',
  ];

  constructor(
    private inventoryService: InventoryService,
    private toastr: ToastService,
    private dialog: MatDialog,
    private categoryService: CategoriesService,
    private brandService: BrandService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.loadProducts(this.currentPage - 1, 'all');
    this.fetchBrands();
    this.fetchCategories();
  }

  loadProducts(page: number, filterValue: string = 'all') {
    this.inventoryService
      .getAllProducts(page, this.pageSize, filterValue)
      .subscribe({
        next: (response) => {
          if (response.status === 'success') {
            this.products = response.data.content.map((product: any) => {
              const mainImage = product.imgUrls?.find((img: any) => img.main);
              return {
                ...product,
                imgUrls: product.imgUrls
                  ? product.imgUrls.map((img: any) => img.imgUrl).join(', ')
                  : 'No Images',
                mainImageUrl: mainImage ? mainImage.imgUrl : null,
                // lowStockProducts: product.stockUnit < 20 ? 1 : 0,
                // lowStockProductsDetails:
                //   product.stockUnit < 50
                //     ? [
                //         {
                //           productId: product.productId,
                //           productName: product.productName,
                //           stockUnit: product.stockUnit,
                //         },
                //       ]
                //     : [],
              };
            });
            this.currentPage = response.data.pageNumber + 1;
            this.totalElements = response.data.totalElements || 0;
            this.totalPages = response.data.totalPages || 1;
          } else {
            this.toastr.showError(response.message);
          }
        },
        error: () => this.toastr.showError('Failed to load products'),
      });
  }

  handleRowAdd(row: any) {
    const productData = {
      ...row,
      categoryId: row.category ? parseInt(row.category, 10) : null,
      category: undefined,
      brandId: row.brand ? parseInt(row.brand, 10) : null,
      brand: undefined,
      hasDiscount: false, // Convert string to boolean
    };
    this.inventoryService.addProduct(productData).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.toastr.showSuccess('Product added successfully', 'Success');
          this.loadProducts(this.currentPage - 1, 'all');
        } else {
          this.toastr.showError(response.message, 'Error');
        }
      },
      error: () => this.toastr.showError('Failed to add product', 'Error'),
    });
  }

  handleRowEdit(row: any) {
    const productData = {
      ...row,
      categoryId: row.category ? parseInt(row.category, 10) : null,
      category: undefined,
      brandId: row.brand ? parseInt(row.brand, 10) : null,
      brand: undefined,
      imgUrls: undefined,
      mainImageUrl: undefined,
      hasDiscount: false, // Convert string to boolean
    };
    this.inventoryService.updateProduct(row.productId, productData).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.toastr.showSuccess('Product updated successfully', 'Success');
          this.loadProducts(this.currentPage - 1, 'all');
        } else {
          this.toastr.showError(response.message, 'Error');
        }
      },
      error: () => this.toastr.showError('Failed to update product', 'Error'),
    });
  }

  handleRowRemove(row: any) {
    this.inventoryService.deleteProduct(row.productId).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.toastr.showSuccess('Product deleted successfully', 'Success');
          this.loadProducts(this.currentPage - 1, 'all');
        } else {
          this.toastr.showError(response.message, 'Error');
        }
      },
      error: () => this.toastr.showError('Failed to delete product', 'Error'),
    });
  }

  handlePageChange(page: number) {
    this.currentPage = page;
    this.loadProducts(
      page - 1,
      this.filterConfig.options.find((opt) => opt.value === 'all')?.value ||
        'all',
    );
  }

  handleFilterChange(filterValue: string) {
    this.loadProducts(0, filterValue);
  }

  openImageUploadDialog(row: any) {
    const dialogRef = this.dialog.open(ImageUploadDialogComponentComponent, {
      width: '600px',
      data: {
        productId: row.productId,
        imgUrls: row.imgUrls ? row.imgUrls.split(', ') : [],
        imageDetails: row.imgUrls
          ? row.imgUrls.split(', ').map((url: string, index: number) => ({
              imgUrl: url,
              main: row.mainImageUrl === url,
              priority: index,
            }))
          : [],
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadProducts(this.currentPage - 1, 'all');
        this.cdr.detectChanges;
      }
    });
  }
  private fetchCategories(): void {
    const sub = this.categoryService.fetchCategories(0, 100).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.categories = response.data.content;
          const categoryColumn = this.columns.find(
            (col) => col.key === 'category',
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
  }

  private fetchBrands(): void {
    this.brandService.fetchBrands(0, 100).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.brands = response.data.content;
          const brandColumn = this.columns.find((col) => col.key === 'brand');
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
  }
}
