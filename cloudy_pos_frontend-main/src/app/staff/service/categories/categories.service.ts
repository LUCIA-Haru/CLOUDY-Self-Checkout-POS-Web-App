import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CategoriesService {
  private baseUrl = '/category';

  constructor(private http: HttpClient) {}

  fetchCategories(
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

  getCategoryById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/get/${id}`);
  }

  addCategory(category: {
    categoryName: string;
    categoryType: string;
    categoryDesc: string;
    aisle: number;
  }): Observable<any> {
    return this.http.post(`${this.baseUrl}/add`, category);
  }

  updateCategory(
    id: number,
    category: {
      categoryName: string;
      categoryType: string;
      categoryDesc: string;
      aisle: number;
    },
  ): Observable<any> {
    return this.http.post(`${this.baseUrl}/update/${id}`, category);
  }

  deleteCategory(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}
