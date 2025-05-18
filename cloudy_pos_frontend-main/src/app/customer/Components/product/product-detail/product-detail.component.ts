import {
  Component,
  OnInit,
  AfterViewInit,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../apiService/productService/product.service';
import { ToastService } from '../../../../Service/toast/toast.service';
import { CommonModule } from '@angular/common';
import { CartService } from 'app/customer/apiService/cartService/cart.service';
import { HttpClient } from '@angular/common/http';
import { MainService } from 'app/Service/main.service';
import { FormsModule } from '@angular/forms';
import { QuantitySelectorComponent } from 'app/Common/quantity-selector/quantity-selector.component';
import { SkeletonLoaderComponent } from 'app/Common/components/skeleton-loader/skeleton-loader.component';
import { AnimationService } from 'app/Common/services/animation.service';
import { gsap } from 'gsap';
@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    QuantitySelectorComponent,
    SkeletonLoaderComponent,
  ],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css'],
})
export class ProductDetailComponent implements OnInit, AfterViewInit {
  product: any;
  barcode: string | null = null;
  loading: boolean = false;
  errorMessage: string = '';
  quantity: number = 1;
  mainImage: string = '';
  thumbnails: string[] = [];

  // ViewChild references to DOM elements for animation
  @ViewChild('mainImageContainer', { static: false }) mainImageRef!: ElementRef;
  @ViewChild('thumbnailContainer', { static: false })
  thumbnailsRef!: ElementRef; // Updated from #thumbnails to #thumbnailContainer
  @ViewChild('productInfo', { static: false }) productInfoRef!: ElementRef;
  @ViewChild('quantitySelector', { static: false })
  quantitySelectorRef!: ElementRef;
  @ViewChild('addToCartButton', { static: false })
  addToCartButtonRef!: ElementRef;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private toastService: ToastService,
    private cartService: CartService,
    private http: HttpClient,
    private mainService: MainService,
    private animationService: AnimationService,
  ) {}

  ngOnInit(): void {
    this.showProductDetail();
  }

  ngAfterViewInit(): void {
    // Animations will be triggered after product data is loaded
    if (this.product) {
      this.animateElements();
      this.addThumbnailHoverEffect();
      this.addButtonHoverEffect();
    }
  }

  showProductDetail(): void {
    if (history.state && history.state.productDetails) {
      this.product = history.state.productDetails.data;
      this.setImages();
      this.animateElements(); // Trigger animations after setting images
    } else {
      this.route.params.subscribe((params) => {
        if (params['barcode']) {
          this.barcode = params['barcode'];
          if (this.barcode) {
            this.fetchProductByBarcode(this.barcode);
          }
        }
      });
    }
  }

  fetchProductByBarcode(barcode: string): void {
    this.loading = true;
    this.productService.getProductByBarcode(barcode).subscribe({
      next: (response: any) => {
        if (response.status === 'success' && response.data) {
          this.product = response.data;
          this.setImages();
          this.loading = false;
          this.animateElements(); // Trigger animations after loading
        } else {
          this.loading = false;
          this.toastService.showError('Product not found!');
        }
      },
      error: (error: any) => {
        console.error('Api error:', error);
        this.goBack();
      },
    });
  }

  setImages(): void {
    if (this.product.imgUrls && this.product.imgUrls.length > 0) {
      const mainImg = this.product.imgUrls.find((img: any) => img.main);
      this.mainImage = mainImg
        ? mainImg.imgUrl
        : this.product.imgUrls[0].imgUrl;
      this.thumbnails = this.product.imgUrls.map((img: any) => img.imgUrl);
    }
  }

  changeMainImage(image: string): void {
    this.mainImage = image;
    // Add a small animation when changing the main image
    gsap.fromTo(
      this.mainImageRef.nativeElement,
      { opacity: 0, scale: 0.95 },
      { opacity: 1, scale: 1, duration: 0.5, ease: 'power2.out' },
    );
  }

  animateElements(): void {
    // Ensure DOM elements are available before animating
    if (!this.mainImageRef || !this.thumbnailsRef || !this.productInfoRef)
      return;

    // Animate Main Image using AnimationService
    this.animationService.fadeIn(this.mainImageRef.nativeElement, 1);

    // Animate Thumbnails using AnimationService
    const thumbnailImages =
      this.thumbnailsRef.nativeElement.querySelectorAll('img');
    this.animationService.staggerFadeIn(thumbnailImages, 1, 0.2);

    // Animate Product Info using AnimationService
    this.animationService.slideInFromLeft(this.productInfoRef.nativeElement, 1);

    // Animate Quantity Selector using AnimationService
    this.animationService.fadeIn(this.quantitySelectorRef.nativeElement, 1);

    // Animate Add to Cart Button using GSAP directly
    gsap.fromTo(
      this.addToCartButtonRef.nativeElement,
      { opacity: 0, y: 30 },
      { opacity: 1, y: 0, duration: 0.7, ease: 'bounce.out', delay: 0.5 },
    );
  }

  goBack(): void {
    this.router.navigate(['/scan']);
  }

  async addItemToCart(product: any, quantity: number): Promise<void> {
    // Add a small animation on button click
    gsap.to(this.addToCartButtonRef.nativeElement, {
      scale: 0.95,
      duration: 0.2,
      yoyo: true,
      repeat: 1,
    });

    try {
      if (!this.mainService.isUserLoggedIn()) {
        this.toastService.showWarning(
          'Please log in to add items to your cart.',
        );
        return;
      }
      // Create a new product object with the mainImage as prodImgURL
      const productWithImage = {
        ...product,
        prodImgURL: this.mainImage, // Use the mainImage as the prodImgURL
      };
      await this.cartService.addToCart(productWithImage, quantity);
      this.router.navigate(['/cart']);
    } catch (error) {
      console.error('Error adding to cart:', error);
      this.toastService.showError('An error occurred while adding to cart.');
    }
  }

  calculateDiscountedPrice(): number {
    if (this.product.hasDiscount && this.product.discount) {
      const discount = this.product.discount;
      if (discount.isPercentage) {
        return this.product.price * (1 - discount.discountValue / 100);
      } else {
        return this.product.price - discount.discountValue;
      }
    }
    return this.product.price;
  }

  onQuantityChange(newQuantity: number): void {
    this.quantity = newQuantity;
  }

  addThumbnailHoverEffect(): void {
    const thumbnailImages =
      this.thumbnailsRef.nativeElement.querySelectorAll('img');
    thumbnailImages.forEach((img: HTMLElement) => {
      gsap.to(img, {
        scale: 1.1,
        duration: 0.3,
        ease: 'power2.out',
        paused: true,
      });

      img.addEventListener('mouseenter', () =>
        gsap.to(img, { scale: 1.1, duration: 0.3 }),
      );
      img.addEventListener('mouseleave', () =>
        gsap.to(img, { scale: 1, duration: 0.3 }),
      );
    });
  }

  addButtonHoverEffect(): void {
    gsap.to(this.addToCartButtonRef.nativeElement, {
      boxShadow: '0 0 15px rgba(0, 255, 0, 0.5)',
      duration: 0.3,
      paused: true,
    });

    this.addToCartButtonRef.nativeElement.addEventListener('mouseenter', () =>
      gsap.to(this.addToCartButtonRef.nativeElement, {
        boxShadow: '0 0 15px rgba(0, 255, 0, 0.5)',
        duration: 0.3,
      }),
    );

    this.addToCartButtonRef.nativeElement.addEventListener('mouseleave', () =>
      gsap.to(this.addToCartButtonRef.nativeElement, {
        boxShadow: 'none',
        duration: 0.3,
      }),
    );
  }
}
