import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { firstValueFrom } from 'rxjs';
import { ToastService } from 'app/Service/toast/toast.service';
import { CartItems } from 'app/customer/Model/cart/cart-items';
import { CartItemDisplay } from 'app/customer/Model/cart/cart-item-display';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = '/product/search';
  private discountApiUrl = '/discount/active';
  private cartItems: CartItems[] = [];
  private cartItemsSubject = new BehaviorSubject<CartItemDisplay[]>([]);
  cartItems$ = this.cartItemsSubject.asObservable();

  constructor(
    private http: HttpClient,
    private toastService: ToastService,
  ) {
    this.loadCartFromStorage();
  }

  private loadCartFromStorage(): void {
    const storedCart = JSON.parse(sessionStorage.getItem('cart') || '[]');
    this.cartItems = storedCart;
    this.updateCartItemsWithDiscounts();
  }

  private saveCart(cart: CartItems[]): void {
    this.cartItems = cart;
    sessionStorage.setItem('cart', JSON.stringify(cart));

    this.updateCartItemsWithDiscounts().then(() => {
      const { totalAmount, originalTotal } = this.calculateCartTotals();
      const totalQuantity = this.getTotalItemCounts();

      sessionStorage.setItem('totalAmount', totalAmount.toString());
      sessionStorage.setItem('originalTotal', originalTotal.toString());
      sessionStorage.setItem('totalQuantity', totalQuantity.toString());
    });
  }

  getCartItems(): CartItems[] {
    return [...this.cartItems];
  }

  getCartItemsWithDiscounts(): CartItemDisplay[] {
    return this.cartItemsSubject.value;
  }

  async addToCart(product: any, quantity: number): Promise<void> {
    if (quantity <= 0 || quantity > product.stockUnit) {
      this.toastService.showWarning(
        'Invalid quantity. Please select a valid amount.',
      );
      return;
    }

    const isValid = await this.validateStock(product.barcode, quantity);
    if (!isValid) {
      this.toastService.showWarning('Stock Not Available!');
      return;
    }

    const cart = this.getCartItems();
    const existingItemIndex = cart.findIndex(
      (item) => item.barcode === product.barcode,
    );

    if (existingItemIndex !== -1) {
      const newQuantity = cart[existingItemIndex].cartQuantity + quantity;
      if (newQuantity > product.stockUnit) {
        this.toastService.showWarning('Cannot exceed available stock!');
        return;
      }
      cart[existingItemIndex].cartQuantity = newQuantity;
    } else {
      cart.push({ ...product, cartQuantity: quantity });
    }

    this.saveCart(cart);
    this.toastService.showSuccess('Item added to cart!');
  }

  async validateStock(barcode: string, quantity: number): Promise<boolean> {
    try {
      const isValid = await firstValueFrom(
        this.http.get<boolean>(`${this.apiUrl}/validate-stock`, {
          params: { barcode, quantity: quantity.toString() },
        }),
      );
      return isValid;
    } catch (error) {
      console.error('Error validating stock:', error);
      return false;
    }
  }

  removeFromCart(barcode: string): void {
    const cart = this.getCartItems().filter((item) => item.barcode !== barcode);
    this.saveCart(cart);
    this.toastService.showSuccess('Item removed from cart!');
  }

  clearCart(): void {
    this.saveCart([]);
    this.toastService.showSuccess('Cart cleared!');
  }

  async updateCartItemQuantity(
    barcode: string,
    quantity: number,
  ): Promise<void> {
    const cart = this.getCartItems();
    const itemIndex = cart.findIndex((item) => item.barcode === barcode);

    if (itemIndex === -1) {
      this.toastService.showWarning('Item not found in the cart!');
      return;
    }

    if (quantity <= 0) {
      this.removeFromCart(barcode);
      return;
    }

    const isValid = await this.validateStock(barcode, quantity);
    if (!isValid) {
      this.toastService.showWarning('Stock Not Available!');
      return;
    }

    cart[itemIndex].cartQuantity = quantity;
    this.saveCart(cart);
    // this.toastService.showSuccess('Cart updated!');
  }

  async fetchActiveDiscount(barcodes: string[]): Promise<any> {
    try {
      const discounts = await firstValueFrom(
        this.http.get<any>(this.discountApiUrl, {
          params: { barcodes: barcodes.join(',') },
        }),
      );
      return discounts;
    } catch (error) {
      console.error('Error fetching discounts:', error);
      return { data: [] };
    }
  }

  private async updateCartItemsWithDiscounts(): Promise<void> {
    const rawCartItems = this.getCartItems();
    if (!rawCartItems.length) {
      this.cartItemsSubject.next([]);
      return;
    }

    const barcodes = rawCartItems.map((item) => item.barcode);
    const discountResponse = await this.fetchActiveDiscount(barcodes);
    const discounts = discountResponse.data;

    const discountMap = new Map<string, any>();
    discounts.forEach((discountObj: any) => {
      discountMap.set(discountObj.barcode, discountObj.discount);
    });

    const transformedItems = rawCartItems.map((item) => {
      const discount = discountMap.get(item.barcode);
      const hasDiscount = !!discount;
      const discountedPrice = this.calculateDiscountedPrice(item, discount);
      return {
        productName: item.productName,
        price: item.price || 0,
        cartQuantity: item.cartQuantity || 1,
        currency: item.currency || 'USD',
        hasDiscount,
        discount,
        prodImgURL: item.prodImgURL || null,
        barcode: item.barcode,
        discountedPrice,
      } as CartItemDisplay;
    });

    this.cartItemsSubject.next(transformedItems);
  }

  private calculateDiscountedPrice(item: CartItems, discount?: any): number {
    let price = item.price || 0;
    if (discount) {
      if (discount.isPercentage) {
        price -= price * ((discount.discountValue || 0) / 100);
      } else {
        price -= discount.discountValue || 0;
      }
    }
    return Math.max(0, price);
  }

  calculateCartTotals(): { totalAmount: number; originalTotal: number } {
    const items = this.getCartItemsWithDiscounts();
    return items.reduce(
      (acc, item) => {
        const discountedPrice = item.discountedPrice || 0;
        const originalPrice = item.price || 0;
        const quantity = item.cartQuantity || 1;
        acc.totalAmount += discountedPrice * quantity;
        acc.originalTotal += originalPrice * quantity;
        return acc;
      },
      { totalAmount: 0, originalTotal: 0 },
    );
  }

  getTotalItemCounts(): number {
    return this.cartItems.reduce(
      (sum, item) => sum + (item.cartQuantity || 1),
      0,
    );
  }
}
