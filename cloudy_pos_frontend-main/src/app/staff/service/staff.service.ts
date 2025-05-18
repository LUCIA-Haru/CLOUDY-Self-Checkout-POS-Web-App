import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StaffService {
  private apiUrl = '/staff';
  private staffUrl = '/staff/register';

  constructor(private http: HttpClient) {}
  // Fetch staff members
  getAllStaff(page: number = 0, size: number = 10): Observable<any> {
    return this.http.get(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  //add staff
  addStaff(staff: any): Observable<any> {
    return this.http.post(this.staffUrl, staff);
  }

  // Update staff member
  updateStaff(staff: any, id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, staff);
  }

  // Delete staff member
  deleteStaff(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
