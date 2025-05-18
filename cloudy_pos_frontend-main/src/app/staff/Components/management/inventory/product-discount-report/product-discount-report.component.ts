import { Component } from '@angular/core';
import {
  Column,
  TableComponent,
} from 'app/Common/components/table/table.component';
import { InventoryService } from 'app/staff/service/inventory/inventory.service';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { ImageUploadDialogComponentComponent } from '../image-upload-dialog-component/image-upload-dialog-component.component';

@Component({
  selector: 'app-product-discount-report',
  imports: [TableComponent],
  templateUrl: './product-discount-report.component.html',
  styleUrl: './product-discount-report.component.css',
})
export class ProductDiscountReportComponent {
  products: any[] = [];
  allProducts: any[] = []; // Store the full dataset
  currentPage: number = 1;
  pageSize: number = 10;
  totalElements: number = 0;
  columns: Column[] = [
    {
      key: 'mainImage',
      label: 'Image',
      sortable: false,
      type: 'text', // Used as a placeholder; rendering is custom
    },
    { key: 'productId', label: 'ID', sortable: true },
    { key: 'productName', label: 'Product Name', sortable: true },
    { key: 'barcode', label: 'Barcode', sortable: true },
    { key: 'price', label: 'Price', sortable: true },
    { key: 'discountValue', label: 'Discount', sortable: true },
    {
      key: 'isPercentage',
      label: 'Discount Type',
      sortable: true,
      type: 'text',
      options: [
        { value: 'true', label: 'Percentage' },
        { value: 'false', label: 'Fixed' },
      ],
    },
    { key: 'discountStartDate', label: 'Start Date', sortable: true },
    { key: 'discountEndDate', label: 'End Date', sortable: true },
    { key: 'stockUnits', label: 'Stock', sortable: true },
  ];
  filterConfig = {
    column: 'isPercentage',
    options: [
      { value: 'all', label: 'All' },
      { value: 'true', label: 'Percentage' },
      { value: 'false', label: 'Fixed' },
    ],
  };

  constructor(
    private inventoryService: InventoryService,
    private toastr: ToastrService,
    private dialog: MatDialog,
  ) {}

  ngOnInit() {
    this.fetchDiscountedProducts();
  }

  fetchDiscountedProducts(filterValue?: string) {
    this.inventoryService.fetchDiscountedProducts().subscribe({
      next: (response: any) => {
        let products = response.data.content;
        this.allProducts = products.map((product: any) => ({
          ...product,
          mainImage: this.getMainImage(product.imgUrls),
          isPercentage: product.isPercentage ? 'Percentage' : 'Fixed',
          discountStartDate: product.discountStartDate || 'N/A',
          discountEndDate: product.discountEndDate || 'N/A',
          stockUnits: product.stockUnits || 0,
        }));

        if (filterValue && filterValue !== 'all') {
          this.allProducts = this.allProducts.filter(
            (product: any) =>
              product.isPercentage.toLowerCase() === filterValue.toLowerCase(),
          );
        }

        this.totalElements = this.allProducts.length;
        this.updatePagination();
        this.updateFilterOptions();
      },
      error: (err: any) => {
        this.toastr.error('Failed to load discounted products');
        console.error(err);
      },
    });
  }

  getMainImage(imgUrls: any[]): string {
    if (!imgUrls || imgUrls.length === 0) return '';
    const mainImage = imgUrls.find((img) => img.main) || imgUrls[0];
    return mainImage ? mainImage.imgUrl : '';
  }

  updateFilterOptions() {
    const uniqueTypes = [
      ...new Set(this.allProducts.map((product) => product.isPercentage)),
    ];
    this.filterConfig.options = [
      { value: 'all', label: 'All' },
      ...uniqueTypes.map((type) => ({
        value: type.toLowerCase(),
        label: type,
      })),
    ];
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.updatePagination();
  }

  onFilterChange(filterValue: string) {
    this.currentPage = 1;
    this.fetchDiscountedProducts(filterValue);
  }

  updatePagination() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.products = this.allProducts.slice(startIndex, endIndex);
  }

  openImageUploadDialog(row: any) {
    const dialogRef = this.dialog.open(ImageUploadDialogComponentComponent, {
      width: '600px',
      data: {
        productId: row.productId,
        imgUrls: row.imgUrls ? row.imgUrls.map((img: any) => img.imgUrl) : [],
        imageDetails: row.imgUrls
          ? row.imgUrls.map((img: any, index: number) => ({
              imgUrl: img.imgUrl,
              main: img.main,
              priority: img.priority || index,
            }))
          : [],
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.fetchDiscountedProducts(); // Refresh the data after image changes
      }
    });
  }
}
