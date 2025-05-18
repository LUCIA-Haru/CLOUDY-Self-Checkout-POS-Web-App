import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Supplier } from 'app/staff/model/Supplier';
import { SupplierTransactionRequest } from 'app/staff/model/SupplierTransaction';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SupplierService {
  private apiUrl = 'http://localhost:8080/suppliers';
  private transactionUrl = '/v1';

  constructor(private http: HttpClient) {}

  // Fetch suppliers
  getSuppliers(page: number = 0, size: number = 10): Observable<any> {
    return this.http.get(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  // Update supplier
  updateSupplier(supplier: Supplier): Observable<any> {
    return this.http.put(this.apiUrl, supplier);
  }

  // Add supplier (assuming the API supports POST; adjust if needed)
  addSupplier(supplier: Supplier): Observable<any> {
    return this.http.post(this.apiUrl, supplier);
  }
  // Delete supplier
  deleteSupplier(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  addTransaction(transaction: SupplierTransactionRequest): Observable<any> {
    return this.http.post(
      `${this.transactionUrl}/supplier-transactions`,
      transaction,
    );
  }

  getAllTransaction(): Observable<any> {
    return this.http.get(`${this.apiUrl}/all/transactions`);
  }
}
