import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InventoryService {
  private productUrl = '/product';
  private reportUrl = '/v1';
  private imageUrl = '/url';

  constructor(private http: HttpClient) {}

  getAllProducts(
    page: number,
    size: number,
    filterValue?: string,
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (filterValue && filterValue !== 'all') {
      params = params.set('filterValue', filterValue);
    }
    return this.http.get(`${this.productUrl}/all`, { params });
  }

  addProduct(product: any): Observable<any> {
    return this.http.post(`${this.productUrl}/add`, product);
  }

  updateProduct(id: number, product: any): Observable<any> {
    return this.http.put(`${this.productUrl}/${id}`, product);
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete(`${this.productUrl}/${id}`);
  }

  uploadProductImages(
    productId: number,
    files: File[],
    priorities: number[],
  ): Observable<any> {
    const formData = new FormData();
    files.forEach((file, index) => {
      formData.append('files', file);
    });
    formData.append('priorities', priorities.join(','));
    return this.http.post(`${this.imageUrl}/product/${productId}`, formData);
  }

  deleteProductImage(productId: number, imgUrl: string): Observable<any> {
    const entity = 'product'; // Hardcoded for this service method
    const params = new HttpParams().set('imgUrl', imgUrl);
    return this.http.delete(`${this.imageUrl}/${entity}/${productId}`, {
      params,
    });
  }

  updateMainImage(productId: number, imgUrl: string): Observable<any> {
    const params = new HttpParams().set('imgUrl', imgUrl);
    return this.http.put(`${this.imageUrl}/product/${productId}/main`, null, {
      params,
    });
  }

  getProductImages(productId: number): Observable<any> {
    return this.http.get(`${this.imageUrl}/fetch/${productId}/product`);
  }

  fetchDiscountedProducts(): Observable<any> {
    return this.http.get(`${this.productUrl}/discounts`);
  }

  fetchExpiryProducts(daysThreshold: number): Observable<any> {
    const params = new HttpParams().set(
      'daysThreshold',
      daysThreshold.toString(),
    );
    return this.http.get(`${this.reportUrl}/product-expiry-alerts`, { params });
  }

  fetchStockLevelsByCategory(): Observable<any> {
    return this.http.get(`${this.reportUrl}/stock-levels-by-category`);
  }
}
