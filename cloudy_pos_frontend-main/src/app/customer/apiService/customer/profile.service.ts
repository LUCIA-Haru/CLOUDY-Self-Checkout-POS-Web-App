import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Customer } from 'app/customer/Model/customer';
import { Staff } from 'app/staff/model/Staff';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private baseUrl = '/customer';
  private baseStaffUrl = '/staff';
  private imgUrl = '/url/customer';
  private imgStaffUrl = '/url/employee';

  constructor(private http: HttpClient) {}

  loadProfile(): Observable<Customer> {
    return this.http
      .get<{
        status: string;
        message: string;
        data: Customer;
      }>(`${this.baseUrl}/get`)
      .pipe(map((response) => response.data));
  }

  updateProfile(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/update`, data);
  }

  uploadProfilePicture(customerId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('files', file);

    const url = `${this.imgUrl}/${customerId}`;
    return this.http.post(url, formData);
  }
  deleteProfilePicture(customerId: number, imgUrl: string): Observable<any> {
    const params = new HttpParams().set('imgUrl', imgUrl);
    const url = `${this.imgUrl}/${customerId}`;
    return this.http.delete(url, { params });
  }

  loadStaffProfile(): Observable<Staff> {
    return this.http
      .get<{
        status: string;
        message: string;
        data: Staff;
      }>(`${this.baseStaffUrl}/get`)
      .pipe(map((response) => response.data));
  }

  updateStaffProfile(data: any): Observable<any> {
    return this.http.post(`${this.baseStaffUrl}/update`, data);
  }

  uploadStaffProfilePicture(id: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('files', file);

    const url = `${this.imgStaffUrl}/${id}`;
    return this.http.post(url, formData);
  }
  deleteStaffProfilePicture(id: number, imgUrl: string): Observable<any> {
    const params = new HttpParams().set('imgUrl', imgUrl);
    const url = `${this.imgStaffUrl}/${id}`;
    return this.http.delete(url, { params });
  }
}
