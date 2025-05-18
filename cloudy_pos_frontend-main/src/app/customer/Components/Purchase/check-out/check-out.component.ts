import {
  Component,
  OnInit,
  Output,
  EventEmitter,
  AfterViewInit,
  ViewChildren,
  QueryList,
  ElementRef,
  ViewChild,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatStepperModule } from '@angular/material/stepper';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { CartService } from 'app/customer/apiService/cartService/cart.service';
import { CheckoutService } from 'app/customer/apiService/checkoutService/checkout.service';
import { AuthService } from 'app/Service/auth/auth.service';
import { ToastService } from 'app/Service/toast/toast.service';
import { Router } from '@angular/router';
import { AnimationService } from 'app/Common/services/animation.service';

@Component({
  selector: 'app-check-out',
  standalone: true,
  imports: [
    MatStepperModule,
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
  ],
  templateUrl: './check-out.component.html',
  styleUrls: ['./check-out.component.css'],
})
export class CheckoutComponent implements OnInit, AfterViewInit {
  @Output() backToCart = new EventEmitter<void>();
  @Output() paymentInitiated = new EventEmitter<any>();

  @ViewChildren('cartItem') cartItems!: QueryList<ElementRef>;
  @ViewChild('confirmOrderBtn', { static: false }) confirmOrderBtn!: ElementRef;

  isPaymentInitiated: boolean = false;
  checkoutData: any;
  checkOutFormGroup: FormGroup;
  dataSource: any[] = [];
  originalTotal: number = 0;
  cartTotal: number = 0;
  couponDiscount: number = 0;
  loyaltyDiscount: number = 0;
  taxAmount: number = 0;
  grandTotal: number = 0;
  userLoyaltyPoints: number = 0;
  username: string = '';

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private checkoutService: CheckoutService,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router,
    private animationService: AnimationService,
  ) {
    this.checkOutFormGroup = this.fb.group({
      couponCode: [''],
      loyaltyPoints: [0, [Validators.min(0)]],
    });
    this.username = this.authService.getUserName();
  }

  async ngOnInit(): Promise<void> {
    if (!this.authService.isLoggedIn()) {
      console.warn('User not logged in. Redirecting to login...');
      this.router.navigate(['/login']);
      return;
    }

    this.dataSource = this.cartService.getCartItemsWithDiscounts();
    const totals = this.cartService.calculateCartTotals();
    this.originalTotal = totals.originalTotal;
    this.cartTotal = totals.totalAmount;
    this.grandTotal = this.cartTotal;

    try {
      const points = await this.checkoutService.fetchLoyaltyPoints(
        this.username,
      );
      this.userLoyaltyPoints = points !== null ? points : 0;
    } catch (error: any) {
      if (error.status === 404) {
        console.log('New user detected; no loyalty points yet.');
        this.userLoyaltyPoints = 0;
      } else {
        console.error('Unexpected error fetching loyalty points:', error);
        this.toastService.showError('Error loading checkout data');
        this.userLoyaltyPoints = 0;
      }
    }

    this.updateTotals();
    this.checkPaymentStatus();
  }

  ngAfterViewInit(): void {
    this.animateCheckoutElements();
  }

  private animateCheckoutElements(): void {
    console.log('Animating checkout elements...');
    if (this.cartItems && this.cartItems.length > 0) {
      this.animationService.staggerFadeIn(
        this.cartItems.map((item) => item.nativeElement),
        0.8,
        0.2,
      );
    }

    if (this.confirmOrderBtn) {
      this.animationService.bounceIn(this.confirmOrderBtn.nativeElement, 0.7);
      this.animationService.pulse(this.confirmOrderBtn.nativeElement, {
        duration: 1.5,
        scale: 1.05,
        repeat: 3,
      });
    }
  }

  private checkPaymentStatus(): void {
    const storedCheckout = sessionStorage.getItem('checkoutData');
    if (storedCheckout) {
      this.checkoutData = JSON.parse(storedCheckout);
      this.isPaymentInitiated = true;
    }
  }

  async applyCoupon(): Promise<void> {
    const couponCode = this.checkOutFormGroup.get('couponCode')?.value;
    this.couponDiscount = await this.checkoutService.applyCoupon(
      couponCode,
      this.cartTotal,
    );
    this.updateTotals();
  }

  applyLoyaltyPoints(): void {
    const points = this.checkOutFormGroup.get('loyaltyPoints')?.value || 0;
    this.loyaltyDiscount = this.checkoutService.validateLoyaltyPoints(
      points,
      this.userLoyaltyPoints,
    );
    this.updateTotals();
  }

  updateTotals(): void {
    const { taxAmount, grandTotal } = this.checkoutService.calculateTotals(
      this.originalTotal,
      this.cartTotal,
      this.couponDiscount,
      this.loyaltyDiscount,
    );
    this.taxAmount = taxAmount;
    this.grandTotal = grandTotal;
  }

  async initiatePurchase(): Promise<void> {
    try {
      const couponCode = this.checkOutFormGroup.get('couponCode')?.value;
      const loyaltyPoints =
        this.checkOutFormGroup.get('loyaltyPoints')?.value || 0;
      const response = await this.checkoutService.initiatePurchase(
        this.username,
        couponCode,
        loyaltyPoints,
      );
      this.couponDiscount = response.data.couponDiscount;
      this.taxAmount = response.data.taxAmount;
      this.grandTotal = response.data.finalAmount;

      if (response.status === 'success') {
        this.isPaymentInitiated = true;
        this.paymentInitiated.emit(response.data);
      }
    } catch (error: any) {
      console.error('Purchase initiation failed:', error);
      this.toastService.showError('Failed to initiate purchase');
      this.isPaymentInitiated = false;
    }
  }
}
