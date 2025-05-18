import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { customer } from 'app/staff/model/Customer';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private apiUrl = 'staff/getAllCustomers';
  constructor(private http: HttpClient) {}
  getAllCustomers(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }
}
