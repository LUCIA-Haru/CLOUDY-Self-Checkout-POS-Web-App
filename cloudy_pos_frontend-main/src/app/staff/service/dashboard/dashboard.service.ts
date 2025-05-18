import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private baseUrl = '/dashboard';

  constructor(private http: HttpClient) {}

  fetchManagerDashboard(): Observable<any> {
    return this.http.get(`${this.baseUrl}/manager`);
  }
  fetchAdminDashboard(): Observable<any> {
    return this.http.get(`${this.baseUrl}/admin`);
  }
  fetchStaffDashboard(): Observable<any> {
    return this.http.get(`${this.baseUrl}/staff`);
  }
}
