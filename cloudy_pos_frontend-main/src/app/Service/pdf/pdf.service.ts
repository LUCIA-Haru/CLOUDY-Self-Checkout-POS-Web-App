import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PdfService {
  private baseUrl = '/customer';

  constructor(private http: HttpClient) {}

  generateVoucher(checkoutData: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/voucher`, checkoutData);
  }

  getPdfUrl(filename: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/voucher/${filename}`, {
      responseType: 'blob',
    });
  }

  getAllVouchers(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/vouchers`);
  }
}
