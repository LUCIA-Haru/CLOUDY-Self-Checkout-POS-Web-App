import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CheckoutService } from 'app/customer/apiService/checkoutService/checkout.service';
import { ToastService } from 'app/Service/toast/toast.service';
import { AuthService } from '../../../../../Service/auth/auth.service';
import { isTokenExpired } from 'app/utils/jwt.utils';
import { CommonModule } from '@angular/common';
import { CheckoutData } from 'app/customer/Model/checkOut';

@Component({
  selector: 'app-paypal-return',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './paypal-return.component.html',
  styleUrls: ['./paypal-return.component.css'],
})
export class PaypalReturnComponent implements OnInit {
  @Input() checkoutData!: CheckoutData;
  @Output() backToCheckout = new EventEmitter<void>();
  @Output() paymentExecuted = new EventEmitter<CheckoutData>();
  @Output() paymentCancelled = new EventEmitter<void>();

  purchaseId: string | null = null;
  paymentId: string | null = null;
  payerId: string | null = null;
  errorMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private checkoutService: CheckoutService,
    private toastService: ToastService,
    private authService: AuthService,
    private router: Router,
  ) {}

  async ngOnInit(): Promise<void> {
    const token = this.getCookie('authToken') || this.authService.getToken();
    if (token && !isTokenExpired(token)) {
      this.authService.setUserFromToken(token);
      this.authService.isLoggedInSubject.next(true);
    } else {
      this.toastService.showError('Session expired. Please log in again.');
      this.router.navigate(['/login']);
      return;
    }

    // Extract query parameters from the PayPal redirect
    this.purchaseId = this.route.snapshot.queryParamMap.get('purchaseId');
    this.paymentId = this.route.snapshot.queryParamMap.get('paymentId');
    this.payerId = this.route.snapshot.queryParamMap.get('PayerID');

    if (!this.paymentId || !this.payerId || !this.purchaseId) {
      this.errorMessage = 'Missing payment details from PayPal.';
      this.toastService.showError(this.errorMessage);
      this.backToCheckout.emit();
      return;
    }

    if (!this.checkoutData) {
      await this.loadCheckoutData();
    }
  }

  async executePayment(): Promise<void> {
    if (!this.purchaseId || !this.paymentId || !this.payerId) {
      this.errorMessage = 'Missing payment parameters';
      this.toastService.showError(this.errorMessage);
      return;
    }

    try {
      const storedCheckoutItems = sessionStorage.getItem('checkoutData');
      if (!storedCheckoutItems) {
        throw new Error('Checkout items not found in session storage.');
      }
      const checkoutItems: any = JSON.parse(storedCheckoutItems);

      const checkoutData: CheckoutData = {
        purchaseId: Number(this.purchaseId),
        cartItems: checkoutItems.cartItems.map((item: any) => ({
          img: item.prodImgURL || item.img || null,
          barcode: item.barcode,
          name: item.name || item.productName,
          price: item.price,
          discountedPrice: item.discountedPrice,
          quantity: item.quantity || item.cartQuantity,
        })),
        originalAmount: checkoutItems.originalAmount,
        totalAmount: checkoutItems.totalAmount,
        couponCode: checkoutItems.couponCode || null,
        couponDiscount: checkoutItems.couponDiscount || 0,
        loyaltyPointsUsed: checkoutItems.loyaltyPointsUsed || 0,
        loyaltyDiscount: checkoutItems.loyaltyDiscount || 0,
        taxAmount: checkoutItems.taxAmount || 0,
        finalAmount: checkoutItems.finalAmount || 0,
        username: checkoutItems.username || this.authService.getUserName(),
        paymentId: this.paymentId,
        payerId: this.payerId,
        pointsEarned: checkoutItems.pointsEarned || 0,
        transactionStatus: checkoutItems.transactionStatus || 'PENDING',
        approvalURl: checkoutItems.approvalURl || null,
        discountAmount:
          (checkoutItems.couponDiscount || 0) +
          (checkoutItems.loyaltyDiscount || 0),
      };

      const response = await this.checkoutService.executePurchasePayment(
        checkoutData,
        Number(this.purchaseId),
        this.paymentId,
        this.payerId,
      );

      if (response.status === 'success') {
        this.toastService.showSuccess('Payment completed successfully!');
        sessionStorage.removeItem('checkoutData');

        // Update checkoutData with response data
        this.checkoutData = {
          ...checkoutData,
          ...response.data,
          transactionStatus: 'COMPLETED',
          pointsEarned:
            response.data.pointsEarned || checkoutData.pointsEarned || 0,
          paymentId: this.paymentId,
          payerId: this.payerId,
        };
        console.log('Updated checkout data:', this.checkoutData);
        this.paymentExecuted.emit(this.checkoutData); // Emit updated checkoutData
      } else {
        this.errorMessage = 'Payment execution failed.';
        this.toastService.showError(this.errorMessage);
      }
    } catch (error) {
      console.error('Error executing payment:', error);
      this.errorMessage = 'Failed to process payment.';
      this.toastService.showError(this.errorMessage);
    }
  }

  async cancelPayment(): Promise<void> {
    if (!this.purchaseId) {
      this.toastService.showError('Missing purchase ID for cancellation');
      return;
    }

    try {
      const response = await this.checkoutService.cancelPurchase(
        Number(this.purchaseId),
      );
      if (response.status === 'success') {
        sessionStorage.removeItem('checkoutData');
        this.paymentCancelled.emit();
      }
    } catch (error) {
      console.error('Error cancelling payment:', error);
      this.toastService.showError('Failed to cancel payment.');
    }
  }

  private getCookie(name: string): string | null {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop()?.split(';').shift() || null;
    return null;
  }

  private async loadCheckoutData(): Promise<void> {
    const storedCheckout = sessionStorage.getItem('checkoutData');
    if (storedCheckout) {
      this.checkoutData = JSON.parse(storedCheckout);
    } else {
      this.errorMessage = 'Checkout data not found.';
      this.toastService.showError(this.errorMessage);
      this.backToCheckout.emit();
    }
  }
}
