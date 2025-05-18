import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';

export interface DiscountDTO {
  discountId: number;
  guid: string;
  productId: number;
  categoryId: number;
  isPercentage: boolean;
  discountValue: number;
  startDate: string;
  endDate: string;
  productName: string;
  categoryName: string;
}

export interface ProductDTO {
  productId: number;
  productName: string;
}

export interface CategoryDTO {
  categoryId: number;
  categoryName: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
}

export interface ApiResponseWrapper<T> {
  status: number;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class DiscountsService {
  private apiUrl = '/discount';
  private productApiUrl = '/product';
  private categoryApiUrl = '/category';

  constructor(private http: HttpClient) {}

  getDiscounts(
    page: number,
    size: number,
    filterValue: string = '',
  ): Observable<PaginatedResponse<DiscountDTO>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('filterValue', filterValue);
    return this.http
      .get<
        ApiResponseWrapper<PaginatedResponse<DiscountDTO>>
      >(`${this.apiUrl}/all`, { params })
      .pipe(map((response) => response.data));
  }

  addDiscount(discount: any): Observable<DiscountDTO> {
    return this.http
      .post<ApiResponseWrapper<DiscountDTO>>(this.apiUrl, discount)
      .pipe(map((response) => response.data));
  }

  updateDiscount(productId: number, discount: any): Observable<DiscountDTO> {
    return this.http
      .post<
        ApiResponseWrapper<DiscountDTO>
      >(`${this.apiUrl}/${productId}`, discount)
      .pipe(map((response) => response.data));
  }

  deleteDiscount(discountId: number): Observable<string> {
    return this.http
      .delete<ApiResponseWrapper<string>>(`${this.apiUrl}/${discountId}`)
      .pipe(map((response) => response.message));
  }

  getProducts(
    page: number,
    size: number,
  ): Observable<PaginatedResponse<ProductDTO>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http
      .get<
        ApiResponseWrapper<PaginatedResponse<ProductDTO>>
      >(`${this.productApiUrl}/all`, { params })
      .pipe(map((response) => response.data));
  }

  getCategories(
    page: number,
    size: number,
  ): Observable<PaginatedResponse<CategoryDTO>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http
      .get<
        ApiResponseWrapper<PaginatedResponse<CategoryDTO>>
      >(`${this.categoryApiUrl}/all`, { params })
      .pipe(map((response) => response.data));
  }
}
