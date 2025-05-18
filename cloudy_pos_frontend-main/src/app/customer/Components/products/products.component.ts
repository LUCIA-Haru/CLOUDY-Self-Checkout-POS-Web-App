import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductAllService } from '../../apiService/productAll/product-all.service';
import { Product } from 'app/customer/Model/productAll';
import { Brand, Category } from 'app/customer/Model/Category&Brand';
import { CardComponent } from 'app/Common/components/card/card.component';
import { FullImageUrlPipe } from 'app/Common/pipe/full-imgae-url/full-image-url.pipe';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

export interface ProductDisplay {
  productId: number;
  productName: string;
  productDesc: string;
  barcode: string;
  price: number;
  currency: string;
  discountedPrice?: number;
  imageUrls: { imgUrl: string }[] | null;
  discounts: { discountValue: number; isPercentage: boolean }[] | null;
}

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, CardComponent, FullImageUrlPipe, FormsModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css'],
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  displayedCategories: Category[] = [];
  brands: Brand[] = [];
  displayedBrands: Brand[] = [];
  filteredProducts: ProductDisplay[] = [];
  selectedFilter: string = 'all'; // Discount filter
  selectedCategories: string[] = [];
  selectedBrands: string[] = [];
  showMoreCategories: boolean = false;
  showMoreBrands: boolean = false;
  searchQuery: string = '';
  discountRanges = [
    { label: 'All', value: 'all' },
    { label: '0-10% Off', value: '0-10' },
    { label: '10-20% Off', value: '10-20' },
    { label: '20%+ Off', value: '20+' },
  ];

  constructor(
    private productService: ProductAllService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadAllProducts();
    this.loadAllCategories();
    this.loadAllBrands();
  }

  loadAllProducts(): void {
    this.productService.getProducts(0, 100).subscribe({
      next: (products: Product[]) => {
        this.products = products;
        this.applyFilters(); // Apply filters on load to show all products by default
      },
      error: (error: any) => {
        console.error('Error fetching products:', error);
      },
    });
  }

  loadAllCategories(): void {
    this.productService.getCategories(0, 100).subscribe({
      next: (categories: Category[]) => {
        this.categories = categories;
        this.displayedCategories = categories.slice(0, 100); // Show first 5 by default
      },
      error: (error: any) => {
        console.error('Error fetching categories:', error);
      },
    });
  }

  loadAllBrands(): void {
    this.productService.getBrands(0, 100).subscribe({
      next: (brands: Brand[]) => {
        this.brands = brands;
        this.displayedBrands = brands.slice(0, 100); // Show first 5 by default
      },
      error: (error: any) => {
        console.error('Error fetching brands:', error);
      },
    });
  }

  transformProducts(products: Product[]): ProductDisplay[] {
    return products.map((product) => {
      const discount =
        product.hasDiscount && product.discount ? product.discount : null;
      const discountedPrice = this.calculateDiscountedPrice(
        product.price,
        discount,
      );
      return {
        productId: product.productId,
        productName: product.productName,
        productDesc: product.productDesc,
        barcode: product.barcode,
        price: product.price,
        currency: product.currency,
        discountedPrice: discountedPrice,
        imageUrls: product.imgUrls
          ? product.imgUrls.map((img) => ({ imgUrl: img.imgUrl }))
          : null,
        discounts: discount
          ? [
              {
                discountValue: discount.discountValue,
                isPercentage: discount.isPercentage,
              },
            ]
          : null,
      };
    });
  }

  calculateDiscountedPrice(
    price: number,
    discount: Product['discount'],
  ): number | undefined {
    if (!discount) return undefined;
    if (discount.isPercentage) {
      return price * (1 - discount.discountValue / 100);
    } else {
      return price - discount.discountValue;
    }
  }

  filterByDiscount(range: string): void {
    this.selectedFilter = range;
    this.applyFilters();
  }

  toggleCategory(categoryName: string): void {
    if (this.selectedCategories.includes(categoryName)) {
      this.selectedCategories = this.selectedCategories.filter(
        (c) => c !== categoryName,
      );
    } else {
      this.selectedCategories.push(categoryName);
    }
  }

  toggleBrand(brandName: string): void {
    if (this.selectedBrands.includes(brandName)) {
      this.selectedBrands = this.selectedBrands.filter((b) => b !== brandName);
    } else {
      this.selectedBrands.push(brandName);
    }
  }

  toggleSection(section: 'categories' | 'brands'): void {
    if (section === 'categories') {
      this.showMoreCategories = !this.showMoreCategories;
      this.displayedCategories = this.showMoreCategories
        ? this.categories
        : this.categories.slice(0, 5);
    } else {
      this.showMoreBrands = !this.showMoreBrands;
      this.displayedBrands = this.showMoreBrands
        ? this.brands
        : this.brands.slice(0, 5);
    }
  }

  toggleShowMore(section: 'categories' | 'brands'): void {
    this.toggleSection(section);
  }

  clearFilters(): void {
    this.selectedCategories = [];
    this.selectedBrands = [];
    this.selectedFilter = 'all';
    this.applyFilters();
  }
  onSearch(): void {
    this.applyFilters();
  }
  applyFilters(): void {
    let filtered = this.products;

    // Apply Search Filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.trim().toLowerCase();
      filtered = filtered.filter((product) =>
        product.productName.toLowerCase().includes(query),
      );
    }

    // Apply Discount Filter
    if (this.selectedFilter !== 'all') {
      const [min, max] =
        this.selectedFilter === '20+'
          ? [20, Infinity]
          : this.selectedFilter.split('-').map(Number);
      filtered = filtered.filter((product) => {
        if (
          !product.hasDiscount ||
          !product.discount ||
          !product.discount.isPercentage
        )
          return false;
        const discountValue = product.discount.discountValue;
        return (
          discountValue >= min && (max === Infinity || discountValue <= max)
        );
      });
    }

    // Apply Category Filter
    if (this.selectedCategories.length > 0) {
      filtered = filtered.filter((product) =>
        this.selectedCategories.includes(product.category),
      );
    }

    // Apply Brand Filter
    if (this.selectedBrands.length > 0) {
      filtered = filtered.filter((product) =>
        this.selectedBrands.includes(product.brand),
      );
    }

    this.filteredProducts = this.transformProducts(filtered);
  }

  viewProduct(barcode: string): void {
    this.router.navigate(['/product/search', barcode]);
  }
}
