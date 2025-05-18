import { DiscountProduct } from './../../Model/discountProduct';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CardComponent } from 'app/Common/components/card/card.component';
import { FullImageUrlPipe } from 'app/Common/pipe/full-imgae-url/full-image-url.pipe';
import { ApiService } from 'app/customer/apiService/api.service';

@Component({
  selector: 'app-discounted-products-lists',
  imports: [CardComponent, CommonModule, FullImageUrlPipe, FormsModule],
  templateUrl: './discounted-products-lists.component.html',
  styleUrls: ['./discounted-products-lists.component.css'],
})
export class DiscountedProductsListsComponent implements OnInit {
  searchQuery: string = '';
  discountProducts: DiscountProduct[] = [];
  filteredProducts: DiscountProduct[] = [];
  discountRanges = [
    { label: 'All Discounts', value: 'all' },
    { label: '10% or more', value: '10' },
    { label: '15% or more', value: '15' },
    { label: '20% or more', value: '20' },
    { label: '30% or more', value: '30' },
    { label: '40% or more', value: '40' },
    { label: '50% or more', value: '50' },
  ];
  selectedFilter = 'all';

  constructor(
    private apiService: ApiService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadDiscountProducts();
  }

  loadDiscountProducts(): void {
    this.apiService.fetchDiscountProducts().subscribe({
      next: (response: { data: { content: any[] } }) => {
        const products = response.data.content;
        this.discountProducts = products.map(
          (product): DiscountProduct => ({
            id: product.productId,
            productName: product.productName,
            productDesc: product.productDesc || 'No description available',
            originalPrice: product.price,
            currency: product.currency,
            discounts: product.discountValue
              ? [
                  {
                    discountValue: product.discountValue,
                    isPercentage: product.isPercentage,
                    startDate: product.discountStartDate,
                    endDate: product.discountEndDate,
                  },
                ]
              : [],
            discountedPrice: this.calculateDiscountedPrice(product),
            imageUrls: this.sortImages(product.imgUrls || []),
            barcode: product.barcode || `barcode-${product.productId}`,
            stockQuantity: product.stockUnits || 0,
          }),
        );
        this.applyFilters(); // Apply filters on load to respect search and discount
      },
      error: (err) => console.error('Error fetching discount products:', err),
    });
  }

  calculateDiscountedPrice(product: {
    price: number;
    discountValue?: number;
    isPercentage?: boolean;
  }): number {
    if (product.discountValue) {
      const discount = product.isPercentage
        ? product.price * (product.discountValue / 100)
        : product.discountValue;
      return parseFloat((product.price - discount).toFixed(2));
    }
    return product.price;
  }

  sortImages(
    imgUrls: { imgUrl: string; priority: number; main: boolean }[],
  ): { imgUrl: string; priority: number; main: boolean }[] {
    if (!imgUrls || imgUrls.length === 0) {
      return [{ imgUrl: '/assets/placeholder.jpg', priority: 0, main: true }];
    }
    return imgUrls.sort((a, b) => (b.main ? 1 : 0) - (a.main ? 1 : 0));
  }

  viewProduct(barcode: string): void {
    this.router.navigate(['/product/search', barcode]);
  }

  onSearch(): void {
    this.applyFilters(); // Trigger filtering on search input
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.applyFilters();
  }

  clearFilters(): void {
    this.searchQuery = '';
    this.selectedFilter = 'all';
    this.applyFilters();
  }

  applyFilters(): void {
    let filtered = [...this.discountProducts];

    // Apply Search Filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.trim().toLowerCase();
      filtered = filtered.filter((product) =>
        product.productName.toLowerCase().includes(query),
      );
    }

    // Apply Discount Filter
    if (this.selectedFilter !== 'all') {
      const minPercentage = parseInt(this.selectedFilter, 10);
      filtered = filtered.filter((product) => {
        const discount = product.discounts[0];
        return (
          discount &&
          discount.isPercentage &&
          discount.discountValue >= minPercentage
        );
      });
    }

    this.filteredProducts = filtered;
  }

  filterByDiscount(range: string = this.selectedFilter): void {
    this.selectedFilter = range;
    this.applyFilters();
  }

  sortByDiscount(ascending: boolean): void {
    this.filteredProducts.sort((a, b) => {
      const discountA = a.discounts[0]?.discountValue || 0;
      const discountB = b.discounts[0]?.discountValue || 0;
      return ascending ? discountA - discountB : discountB - discountA;
    });
  }

  filterProducts(
    minPrice: number,
    maxPrice: number,
    minDiscount: number,
  ): void {
    this.filteredProducts = this.discountProducts.filter((product) => {
      const meetsPrice =
        product.discountedPrice >= minPrice &&
        product.discountedPrice <= maxPrice;
      const discount = product.discounts[0];
      const meetsDiscount =
        !minDiscount ||
        (discount &&
          discount.isPercentage &&
          discount.discountValue >= minDiscount);
      return meetsPrice && meetsDiscount;
    });
  }
}
