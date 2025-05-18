import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private discountProductUrl = '/product/discounts';

  private loyaltyPointsUrl = 'v1/transactions/loyaltypoints';

  private couponUrl = '/coupon/cus';

  private customerUrl = '/customer';

  private userUrl = '/user';

  constructor(private http: HttpClient) {}

  fetchDiscountProducts(): Observable<{
    status: string;
    message: string;
    data: {
      content: {
        productId: number;
        productName: string;
        productDesc?: string;
        price: number;
        currency: string;
        discountValue?: number;
        isPercentage?: boolean;
        discountStartDate?: string;
        discountEndDate?: string;
        stockUnits?: number;
        imgUrls: { imgUrl: string; priority: number; main: boolean }[];
        barcode?: string;
      }[];
      total: number;
    };
  }> {
    return this.http
      .get<{
        status: string;
        message: string;
        data: {
          content: {
            productId: number;
            productName: string;
            productDesc?: string;
            price: number;
            currency: string;
            discountValue?: number;
            isPercentage?: boolean;
            discountStartDate?: string;
            discountEndDate?: string;
            stockUnits?: number;
            imgUrls: { imgUrl: string; priority: number; main: boolean }[];
            barcode?: string;
          }[];
          total: number;
        };
      }>(this.discountProductUrl)
      .pipe(
        catchError((error) => {
          console.error('Discount products API error:', error);
          return throwError(
            () => new Error('Failed to fetch discount products'),
          );
        }),
      );
  }

  // Fetch loyalty points history for customer
  fetchLoyaltyPointsHistory(): Observable<any> {
    return this.http.get<any>(this.loyaltyPointsUrl).pipe(
      catchError((error) => {
        console.error('Loyalty points API error:', error);
        return throwError(
          () => new Error('Failed to fetch loyalty points history'),
        );
      }),
    );
  }

  // Fetch coupons for customer
  fetchCouponsHistory(): Observable<any> {
    return this.http.get<any>(this.couponUrl).pipe(
      catchError((error) => {
        console.error('Coupons API error:', error);
        return throwError(() => new Error('Failed to fetch coupons history'));
      }),
    );
  }

  fetchPurchaseHistory(): Observable<any> {
    return this.http.get<any>(`${this.customerUrl}/history`).pipe(
      catchError((error) => {
        console.error('Coupons API error:', error);
        return throwError(() => new Error('Failed to fetch coupons history'));
      }),
    );
  }

  downloadPdf(filename: string): Observable<Blob> {
    const url = `${this.customerUrl}/voucher/${filename}`;
    return this.http.get(url, {
      responseType: 'blob',
    });
  }
}
