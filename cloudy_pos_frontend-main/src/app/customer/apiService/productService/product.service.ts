import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = '/product';
  constructor(private http: HttpClient) {}

  getProductByBarcode(barcode: string): Observable<any> {
    const url = `${this.apiUrl}/search/${barcode}`;
    const response = this.http.get<any>(url);
    return response;
  }
}
