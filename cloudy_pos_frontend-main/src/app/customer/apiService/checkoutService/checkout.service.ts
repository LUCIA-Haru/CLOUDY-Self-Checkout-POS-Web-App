import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CartService } from '../cartService/cart.service';
import { ToastService } from 'app/Service/toast/toast.service';
import { firstValueFrom } from 'rxjs';
import { AuthService } from 'app/Service/auth/auth.service';
import { isTokenExpired } from 'app/utils/jwt.utils';
import { Router } from '@angular/router';
import { CheckoutData } from 'app/customer/Model/checkOut';

interface CheckoutResponse {
  status: string;
  message: string;
  data: CheckoutData;
}
interface CancelResponse {
  status: string;
  message: string;
  data: string; // Based on your backend signature: ApiResponseWrapper<String>
}
@Injectable({
  providedIn: 'root',
})
export class CheckoutService {
  private baseUrl = '/v1';
  private couponUrl = '/coupon';
  private TAX_RATE = 0.3;

  constructor(
    private http: HttpClient,
    private cartService: CartService,
    private toastService: ToastService,
    private authService: AuthService,
    private router: Router,
  ) {}
  // loyalty points
  async fetchLoyaltyPoints(username: string): Promise<number | null> {
    const token = this.authService.getToken();
    if (!token || isTokenExpired(token)) {
      console.warn('No valid token for loyalty points fetch.');
      throw new Error('User not authenticated');
    }

    try {
      const points = await firstValueFrom(
        this.http.get<number>(
          `${this.baseUrl}/purchase/${username}/loyaltyPoints`,
        ),
      );
      return points || 0;
    } catch (error: any) {
      if (error.status === 404) {
        return null; // No loyalty points record exists
      }
      console.error('Error fetching loyalty points:', error);
      this.toastService.showError('Failed to fetch loyalty points');
      throw error; // Re-throw for other errors
    }
  }
  // coupon
  async applyCoupon(couponCode: string, totalAmount: number): Promise<number> {
    if (!couponCode) {
      this.toastService.showError('Please enter a valid coupon code');
      return 0;
    }

    try {
      const response: any = await firstValueFrom(
        this.http.post(`${this.couponUrl}/apply`, {
          couponCode,
          totalAmount: totalAmount.toString(),
        }),
      );
      if (response.status === 'success') {
        const discount = parseFloat(response.data) || 0;
        return discount;
      } else {
        this.toastService.showError(
          response.message || 'Failed to apply coupon',
        );
        return 0;
      }
    } catch (error) {
      console.error('Error applying coupon:', error);
      this.toastService.showError('Failed to apply coupon');
      return 0;
    }
  }

  // validate loyalty points
  validateLoyaltyPoints(
    points: number,
    availablePoints: number | null,
  ): number {
    const effectivePoints = availablePoints ?? 0; // Treat null as 0
    if (points > effectivePoints) {
      this.toastService.showWarning('Insufficient loyalty points.');
      return 0;
    } else if (points < 0) {
      this.toastService.showWarning('Points cannot be negative.');
      return 0;
    } else {
      return points; // 1 point = 1 USD
    }
  }
  // calculate totals
  calculateTotals(
    originalTotal: number,
    cartTotal: number,
    couponDiscount: number,
    loyaltyDiscount: number,
  ): { taxAmount: number; grandTotal: number } {
    const subtotal = cartTotal - couponDiscount - loyaltyDiscount;
    const taxAmount = subtotal * this.TAX_RATE;
    const grandTotal = subtotal + taxAmount;
    return { taxAmount, grandTotal: grandTotal < 0 ? 0 : grandTotal };
  }

  // initiate purchase
  async initiatePurchase(
    username: string,
    couponCode: string | null,
    loyaltyPointsUsed: number,
  ): Promise<CheckoutResponse> {
    const cartItems = this.cartService.getCartItemsWithDiscounts();
    const totals = this.cartService.calculateCartTotals();
    const couponDiscount = couponCode
      ? await this.applyCoupon(couponCode, totals.totalAmount)
      : 0;
    const availablePoints = await this.fetchLoyaltyPoints(username);
    const loyaltyDiscount = this.validateLoyaltyPoints(
      loyaltyPointsUsed,
      availablePoints,
    );
    const { taxAmount, grandTotal } = this.calculateTotals(
      totals.originalTotal,
      totals.totalAmount,
      couponDiscount,
      loyaltyDiscount,
    );

    const checkoutData: CheckoutData = {
      cartItems: cartItems.map((item) => ({
        img: item.prodImgURL || undefined,
        barcode: item.barcode,
        name: item.productName,
        price: item.price,
        discountedPrice: item.discountedPrice,
        quantity: item.cartQuantity,
      })),
      originalAmount: totals.originalTotal,
      totalAmount: totals.totalAmount, // After discount
      couponCode: couponCode || null,
      couponDiscount: couponDiscount || 0,
      loyaltyPointsUsed: loyaltyPointsUsed || 0,
      loyaltyDiscount: loyaltyDiscount || 0,
      taxAmount,
      finalAmount: grandTotal,
      username,
    };
    sessionStorage.setItem('checkoutData', JSON.stringify(checkoutData));
    const token = this.authService.getToken();
    if (!token) {
      this.toastService.showError('Token is missing. Please log in again.');
      this.router.navigate(['/login']);
      throw new Error('Token is missing. Please log in again.');
    }
    // document.cookie = `authToken=${token}; path=/; SameSite=Lax`;
    try {
      const response: CheckoutResponse = await firstValueFrom(
        this.http.post<CheckoutResponse>(
          `${this.baseUrl}/purchase`,
          checkoutData,
        ),
      );
      if (response.status === 'success' && response.data?.approvalURl) {
        this.toastService.showSuccess(
          'Payment initiated! Redirecting to PayPal...',
        );
        sessionStorage.setItem(
          'checkoutData',
          JSON.stringify({
            ...checkoutData,
            purchaseId: response.data.purchaseId,
            paymentId: response.data.paymentId,
          }),
        );
        window.location.href = response.data.approvalURl; // Redirect to PayPal
      } else {
        this.toastService.showError(
          response.message || 'Failed to initiate payment.',
        );
      }
      return response;
    } catch (error: any) {
      console.error('Checkout error:', error);
      this.toastService.showError(
        'Checkout failed: ' + (error.error?.message || 'Something went wrong'),
      );
      throw error;
    }
  }
  // execute purchase payment
  async executePurchasePayment(
    checkoutData: CheckoutData,
    purchaseId: number,
    paymentId: string,
    payerId: string,
  ): Promise<CheckoutResponse> {
    debugger;
    const token = this.authService.getToken();
    if (!token) {
      throw new Error('No authentication token available');
    }

    const url = `${this.baseUrl}/purchase/${purchaseId}/payment/execute?paymentId=${paymentId}&PayerID=${payerId}`;
    console.log('Payload:', checkoutData); // Debug log
    try {
      const response: CheckoutResponse = await firstValueFrom(
        this.http.post<CheckoutResponse>(url, checkoutData, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }),
      );
      if (response.status === 'success') {
        this.toastService.showSuccess('Payment completed successfully!');
        sessionStorage.removeItem('paymentDetails');
      }
      return response;
    } catch (error: any) {
      console.error('Payment execution error:', error);
      this.toastService.showError('Payment execution failed: ');
      throw error;
    }
  }

  // handle paypal cancel
  async cancelPurchase(purchaseId: number): Promise<CancelResponse> {
    const token = this.authService.getToken();
    if (!token) {
      this.toastService.showError('Token is missing. Please log in again.');
      throw new Error('No authentication token available');
    }

    const url = `${this.baseUrl}/purchase/${purchaseId}/cancel`;
    try {
      const response: CancelResponse = await firstValueFrom(
        this.http.post<CancelResponse>(
          url,
          {},
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          },
        ),
      );

      if (response.status === 'success') {
        this.toastService.showSuccess('Purchase cancelled successfully!');
      } else {
        this.toastService.showError(
          response.message || 'Failed to cancel purchase.',
        );
      }
      return response;
    } catch (error: any) {
      console.error('Cancel purchase error:', error);
      this.toastService.showError(
        'Failed to cancel purchase: ' +
          (error.error?.message || 'Something went wrong'),
      );
      throw error;
    }
  }
}
