import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CouponsService {
  private baseUrl = '/coupon';
  private customersUrl = '/staff/getAllCustomers';

  constructor(private http: HttpClient) {}

  fetchCoupons(): Observable<any> {
    return this.http.get(`${this.baseUrl}/admin`);
  }

  fetchCustomers(): Observable<any> {
    return this.http.get(this.customersUrl);
  }

  assignCoupon(cusId: number, couponCode: string): Observable<any> {
    const params = { cusId: cusId.toString(), CouponCode: couponCode };
    return this.http.post(`${this.baseUrl}/assign`, null, { params });
  }

  generateCoupons(coupon: {
    discountAmount: number;
    minPurchaseAmount: number;
    expirationDate: Date;
  }): Observable<any> {
    return this.http.post(`${this.baseUrl}`, coupon);
  }

  updateCoupon(
    coupon: {
      discountAmount: number;
      minPurchaseAmount: number;
      expirationDate: Date;
    },
    couponId: number,
  ): Observable<any> {
    return this.http.put(`${this.baseUrl}/${couponId}`, coupon);
  }

  deleteCoupon(couponId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${couponId}`);
  }
}
