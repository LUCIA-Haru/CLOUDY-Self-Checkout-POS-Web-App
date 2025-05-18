import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ApiService } from 'app/customer/apiService/api.service';
import { DiscountProduct } from 'app/customer/Model/discountProduct';
import { CarouselsComponent } from 'app/Common/components/carousels/carousels.component';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-discount-products',
  standalone: true,
  imports: [CommonModule, CarouselsComponent, MatIconModule],
  templateUrl: './discount-products.component.html',
  styleUrls: ['./discount-products.component.css'],
})
export class DiscountProductsComponent implements OnInit {
  discountProducts: DiscountProduct[] = [];
  loading: boolean = true;
  error: string | null = null;

  constructor(
    private apiService: ApiService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadDiscountProducts();
  }

  loadDiscountProducts(): void {
    this.loading = true;
    this.apiService.fetchDiscountProducts().subscribe({
      next: (response: { data: { content: any[] } }) => {
        console.log('Discount products response:', response);
        if (!response.data || !response.data.content) {
          this.error = 'No products found';
          this.loading = false;
          return;
        }
        const products = response.data.content.slice(0, 9);
        console.log('Products after slice:', products);
        this.discountProducts = products.map((product): DiscountProduct => {
          const mappedProduct: DiscountProduct = {
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
          };
          console.log('Mapped product:', mappedProduct);
          return mappedProduct;
        });
        console.log('Final discountProducts:', this.discountProducts);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching discount products:', err);
        this.error = 'Failed to load products';
        this.loading = false;
      },
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

  viewAll(): void {
    console.log('View All clicked');
    this.router.navigate(['/discount/all']);
  }
}
