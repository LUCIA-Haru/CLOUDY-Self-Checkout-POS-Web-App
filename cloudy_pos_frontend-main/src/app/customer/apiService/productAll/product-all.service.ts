import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Product } from 'app/customer/Model/productAll';
import { Category } from 'app/customer/Model/Category&Brand';
import { Brand } from 'app/customer/Model/Category&Brand';

@Injectable({
  providedIn: 'root',
})
export class ProductAllService {
  private productUrl = '/product/all';
  private categoryUrl = '/category/all';
  private brandUrl = '/brand/all';

  constructor(private http: HttpClient) {}

  getProducts(page: number, size: number): Observable<Product[]> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http
      .get<{
        status: string;
        message: string;
        data: { content: Product[] };
      }>(this.productUrl, { params })
      .pipe(map((response) => response.data.content));
  }

  getCategories(page: number, size: number): Observable<Category[]> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http
      .get<{
        status: string;
        message: string;
        data: { content: Category[] };
      }>(this.categoryUrl, { params })
      .pipe(map((response) => response.data.content));
  }

  getBrands(page: number, size: number): Observable<Brand[]> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http
      .get<{
        status: string;
        message: string;
        data: { content: Brand[] };
      }>(this.brandUrl, { params })
      .pipe(map((response) => response.data.content));
  }
}
