import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BrandService {
  private baseUrl = '/brand';

  constructor(private http: HttpClient) {}

  fetchBrands(
    page: number,
    size: number,
    filterValue?: string,
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (filterValue) {
      params = params.set('filterValue', filterValue);
    }
    return this.http.get(`${this.baseUrl}/all`, { params });
  }

  getBrandById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addBrand(brand: { name: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}`, brand);
  }

  updateBrand(brand: { id: number; name: string }): Observable<any> {
    return this.http.put(`${this.baseUrl}`, brand);
  }

  deleteBrand(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}
