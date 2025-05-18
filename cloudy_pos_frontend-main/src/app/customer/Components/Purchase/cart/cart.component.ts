import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ViewChildren,
  QueryList,
  ElementRef,
  inject,
  ChangeDetectorRef,
} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { CartService } from '../../../apiService/cartService/cart.service';
import { ToastService } from 'app/Service/toast/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { CommonModule } from '@angular/common';
import { QuantitySelectorComponent } from './../../../../Common/quantity-selector/quantity-selector.component';
import {
  trigger,
  state,
  style,
  animate,
  transition,
} from '@angular/animations';
import { CartItemDisplay } from 'app/customer/Model/cart/cart-item-display';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { CheckoutComponent } from '../check-out/check-out.component';
import { PaypalReturnComponent } from '../paypal/paypal-return/paypal-return.component';
import { CheckoutData } from 'app/customer/Model/checkOut';
import { VoucherComponent } from '../voucher/voucher/voucher.component';
import { AnimationService } from 'app/Common/services/animation.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmComponent } from 'app/Common/dialog/confirm/confirm.component';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    QuantitySelectorComponent,
    CommonModule,
    MatStepperModule,
    CheckoutComponent,
    PaypalReturnComponent,
    VoucherComponent,
  ],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  animations: [
    trigger('fadeIn', [
      state('void', style({ opacity: 0, transform: 'translateY(20px)' })),
      transition(':enter', [
        animate(
          '600ms ease-out',
          style({ opacity: 1, transform: 'translateY(0)' }),
        ),
      ]),
    ]),
    trigger('hoverEffect', [
      state('normal', style({ transform: 'scale(1)' })),
      state(
        'hovered',
        style({
          transform: 'scale(1.02)',
          boxShadow: '0 4px 15px rgba(0,0,0,0.2)',
        }),
      ),
      transition('normal <=> hovered', animate('200ms ease-in-out')),
    ]),
  ],
})
export class CartComponent implements OnInit, AfterViewInit {
  isCartEmpty: boolean = true;
  displayedColumns: string[] = [
    'Image',
    'productName',
    'originalPrice',
    'discountedPrice',
    'quantity',
    'total',
    'actions',
  ];
  dataSource = new MatTableDataSource<CartItemDisplay>();
  cartTotal: number = 0;
  originalTotal: number = 0;
  totalCounts: number = 0;
  title = 'Your Cart';
  isLinear = true;
  checkoutData: CheckoutData | undefined;
  purchaseData: any;
  private pendingRedirect: boolean = false;
  private hasProcessedPayment: boolean = false;

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild('stepper') stepper!: MatStepper;
  @ViewChild('cartItemsContainer', { static: false })
  cartItemsContainer!: ElementRef;
  @ViewChild('cartFooter', { static: false }) cartFooter!: ElementRef;
  @ViewChild('summaryCard', { static: false }) summaryCard!: ElementRef;
  @ViewChild('secondaryActions', { static: false })
  secondaryActions!: ElementRef;
  @ViewChild('addMoreBtn', { static: false }) addMoreBtn!: ElementRef;
  @ViewChild('clearCartBtn', { static: false }) clearCartBtn!: ElementRef;
  @ViewChild('checkoutBtn', { static: false }) checkoutBtn!: ElementRef;
  @ViewChildren('cartItem') cartItems!: QueryList<ElementRef>;

  private _formBuilder = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);
  private animationService = inject(AnimationService);
  cartFormGroup: FormGroup;
  checkOutFormGroup: FormGroup;
  paymentFormGroup: FormGroup;

  constructor(
    private cartService: CartService,
    private toastService: ToastService,
    private router: Router,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private dialog: MatDialog,
  ) {
    this.cartFormGroup = this.fb.group({});
    this.checkOutFormGroup = this.fb.group({
      couponCode: [''],
      loyaltyPoints: [''],
    });
    this.paymentFormGroup = this.fb.group({});
  }

  ngOnInit(): void {
    this.cartService.cartItems$.subscribe((items) => {
      console.log('Cart items:', items);
      this.dataSource.data = items;
      this.cartTotal = this.cartService.calculateCartTotals().totalAmount;
      this.totalCounts = this.cartService.getTotalItemCounts();
      this.isCartEmpty = items.length === 0;
      this.cdr.detectChanges();
    });

    const params = this.route.snapshot.queryParams;
    if (params['purchaseId'] && params['paymentId'] && params['PayerID']) {
      this.handlePaypalRedirect();
    }
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'total':
          return item.discountedPrice * item.cartQuantity;
        default:
          return (item as any)[property];
      }
    };

    if (this.pendingRedirect && this.stepper) {
      console.log('Processing pending PayPal redirect');
      this.handlePaypalRedirect();
    }

    this.route.queryParams.subscribe((params) => {
      if (params['purchaseId'] && params['paymentId'] && params['PayerID']) {
        console.log('Dynamic PayPal redirect params detected');
        if (this.stepper) {
          this.handlePaypalRedirect();
        } else {
          console.log('Stepper not ready, setting pending redirect');
          this.pendingRedirect = true;
        }
      }
    });

    this.animateCartElements();
  }

  animateCartElements(): void {
    if (this.cartItems && this.cartItems.length > 0) {
      this.animationService.staggerFadeIn(
        this.cartItems.map((item) => item.nativeElement),
        0.8,
        0.2,
      );
    }

    if (this.summaryCard) {
      this.animationService.bounceIn(this.summaryCard.nativeElement, 0.8);
    }

    if (this.secondaryActions) {
      const buttons =
        this.secondaryActions.nativeElement.querySelectorAll('button');
      this.animationService.staggerFadeIn(buttons, 0.7, 0.3);
    }

    if (this.checkoutBtn) {
      this.animationService.bounceIn(this.checkoutBtn.nativeElement, 0.7);
      this.animationService.pulse(this.checkoutBtn.nativeElement, {
        duration: 1.5,
        scale: 1.05,
      });
    }
  }

  async updateQuantity(barcode: string, quantity: number): Promise<void> {
    try {
      await this.cartService.updateCartItemQuantity(barcode, quantity);
    } catch (error) {
      this.toastService.showError('Error updating quantity');
      console.error('Error updating quantity:', error);
    }
  }

  removeFromCart(barcode: string): void {
    try {
      this.cartService.removeFromCart(barcode);
      setTimeout(() => this.animateCartElements(), 0);
    } catch (error) {
      this.toastService.showError('Error removing item');
      console.error('Error in removeFromCart:', error);
    }
  }

  clearCart(): void {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      width: '350px',
      data: {
        title: 'Clear The Cart',
        message: 'Are you sure you want to clear the cart?',
      },
    });
    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        try {
          this.cartService.clearCart();
          this.checkoutData = undefined;
          this.purchaseData = undefined;
          this.hasProcessedPayment = false;
        } catch (error) {
          this.toastService.showError('Error clearing cart');
          console.error('Error in clearCart:', error);
        }
      }
    });
  }

  scan(): void {
    this.router.navigate(['/scan']);
  }

  handleBackToCart(): void {
    console.log('Back to Cart event received');
    this.stepper.previous();
    this.cdr.detectChanges();
  }

  handlePaymentInitiated(checkoutData: CheckoutData): void {
    this.checkoutData = checkoutData;
    this.navigateToStep(2); // Move to Payment step
  }

  handlePaymentCancelled(): void {
    this.navigateToStep(0);
    sessionStorage.removeItem('checkoutData');
    this.checkoutData = undefined;
    this.purchaseData = undefined;
    this.hasProcessedPayment = false;
    this.cdr.detectChanges();
  }

  private navigateToStep(index: number): void {
    if (this.stepper) {
      console.log('Navigating to step:', index);
      this.stepper.selectedIndex = index;
      this.cdr.detectChanges();
    } else {
      console.warn('Stepper not initialized, retrying navigation');
      setTimeout(() => {
        if (this.stepper) {
          console.log('Retry navigation to step:', index);
          this.stepper.selectedIndex = index;
          this.cdr.detectChanges();
        } else {
          console.error('Stepper still not initialized, navigation failed');
        }
      }, 100);
    }
  }

  private handlePaypalRedirect(): void {
    this.pendingRedirect = false;
    if (this.hasProcessedPayment) {
      console.log('Payment already processed, skipping redirect handling');
      return;
    }
    const storedCheckout = sessionStorage.getItem('checkoutData');
    if (storedCheckout) {
      try {
        this.checkoutData = JSON.parse(storedCheckout);
        console.log('Checkout data loaded:', this.checkoutData);
        this.navigateToStep(2);
      } catch (error) {
        console.error('Error parsing checkout data:', error);
        sessionStorage.removeItem('checkoutData');
        this.navigateToStep(0);
      }
    } else {
      console.warn('No checkout data found in session storage');
      sessionStorage.removeItem('checkoutData');
      this.navigateToStep(0);
    }
  }

  handlePaymentExecuted(checkoutData: CheckoutData): void {
    this.pendingRedirect = false;
    this.hasProcessedPayment = true;
    this.purchaseData = checkoutData;
    this.checkoutData = checkoutData;
    console.log('Payment executed:', this.checkoutData);
    this.navigateToStep(3); // Move to Done step
    this.cdr.detectChanges();
  }

  search(barcode: string): void {
    this.router.navigate(['/product/search', barcode]);
  }
}
