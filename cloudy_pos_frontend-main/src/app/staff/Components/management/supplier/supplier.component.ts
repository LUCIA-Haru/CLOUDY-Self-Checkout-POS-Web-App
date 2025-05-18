import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { SupplierService } from 'app/staff/service/supplier/supplier.service';
import { Supplier } from 'app/staff/model/Supplier';
import { interval, Subscription, switchMap } from 'rxjs';
import {
  TableComponent,
  Column,
} from 'app/Common/components/table/table.component';
import { ToastService } from 'app/Service/toast/toast.service';

@Component({
  selector: 'app-supplier',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    TableComponent,
  ],
  templateUrl: './supplier.component.html',
  styleUrls: ['./supplier.component.css'],
})
export class SupplierComponent implements OnInit, OnDestroy {
  allSuppliers: Supplier[] = [];
  columns: Column[] = [
    { key: 'id', label: 'ID', sortable: true, editable: false },
    {
      key: 'name',
      label: 'Name',
      sortable: true,
      type: 'text',
      required: true,
    },
    { key: 'contactEmail', label: 'Email', type: 'text', required: true },
    { key: 'contactPhone', label: 'Phone', type: 'text', required: true },
    {
      key: 'isMainSupplier',
      label: 'Main Supplier',
      type: 'select',
      options: [
        { value: 'true', label: 'Yes' },
        { value: 'false', label: 'No' },
      ],
      required: true,
    },
    {
      key: 'contractDurationInMonths',
      label: 'Contract Duration (Months)',
      type: 'number',
      required: true,
    },
    {
      key: 'isActive',
      label: 'Status',
      type: 'select',
      options: [
        { value: 'true', label: 'Active' },
        { value: 'false', label: 'Inactive' },
      ],
      required: true,
    },
    { key: 'createdOn', label: 'Created On', type: 'date', editable: false },
    { key: 'createdBy', label: 'Created By', type: 'text', editable: false },
    { key: 'updatedOn', label: 'Updated On', type: 'date', editable: false },
    { key: 'updatedBy', label: 'Updated By', type: 'text', editable: false },
  ];

  filterConfig = {
    column: 'isActive',
    options: [
      { value: 'all', label: 'All' },
      { value: 'true', label: 'Active' },
      { value: 'false', label: 'Inactive' },
    ],
  };

  currentPage: number = 1;
  pageSize: number = 10;
  isLoading: boolean = true;
  errorMessage: string | null = null;

  private pollingSubscription: Subscription | null = null;

  constructor(
    private supplierService: SupplierService,
    private cdr: ChangeDetectorRef,
    private toast: ToastService,
  ) {}

  ngOnInit(): void {
    this.loadAllSuppliers();
    this.startPolling();
  }

  ngOnDestroy(): void {
    this.stopPolling();
  }

  loadAllSuppliers(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.supplierService.getSuppliers(0, 1000).subscribe({
      next: (response) => {
        this.allSuppliers = response.data?.content || response.content || [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = 'Failed to load suppliers. Please try again later.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      complete: () => {
        console.log('API call completed');
      },
    });
  }

  startPolling(): void {
    this.pollingSubscription = interval(30000)
      .pipe(
        switchMap(() => {
          console.log('Polling: Calling SupplierService.getSuppliers(0, 1000)');
          return this.supplierService.getSuppliers(0, 1000);
        }),
      )
      .subscribe({
        next: (response) => {
          this.allSuppliers = response.data?.content || response.content || [];
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Polling error:', err),
      });
  }

  stopPolling(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
      this.pollingSubscription = null;
    }
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.cdr.detectChanges();
  }

  addSupplier(newSupplier: Supplier): void {
    const formattedSupplier: Supplier = {
      name: newSupplier.name!,
      contactEmail: newSupplier.contactEmail!,
      contactPhone: newSupplier.contactPhone!,
      isMainSupplier:
        String(newSupplier.isMainSupplier) === 'true' ||
        newSupplier.isMainSupplier === true,
      contractDurationInMonths: parseInt(
        newSupplier.contractDurationInMonths as any,
        10,
      ),
      isActive:
        String(newSupplier.isActive) === 'true' ||
        newSupplier.isActive === true,
      id: 0,
    };

    this.supplierService.addSupplier(formattedSupplier).subscribe({
      next: (response) => {
        console.log('Supplier added successfully:', response);
        this.toast.showSuccess('Supplier added successfully');
        this.loadAllSuppliers();
      },
      error: (err) => {
        console.error('Error adding supplier:', err);
        this.toast.showError('Failed to add supplier. Please try again.');
        this.cdr.detectChanges();
      },
    });
  }

  updateSupplier(updatedSupplier: Supplier): void {
    const formattedSupplier: Supplier = {
      id: updatedSupplier.id,
      name: updatedSupplier.name!,
      contactEmail: updatedSupplier.contactEmail!,
      contactPhone: updatedSupplier.contactPhone!,
      isMainSupplier:
        String(updatedSupplier.isMainSupplier) === 'true' ||
        updatedSupplier.isMainSupplier === true,
      contractDurationInMonths: parseInt(
        updatedSupplier.contractDurationInMonths as any,
        10,
      ),
      isActive:
        String(updatedSupplier.isActive) === 'true' ||
        updatedSupplier.isActive === true,
    };

    this.supplierService.updateSupplier(formattedSupplier).subscribe({
      next: (response) => {
        console.log('Supplier updated successfully:', response);
        this.toast.showSuccess('Supplier updated successfully');
        this.loadAllSuppliers();
      },
      error: (err) => {
        console.error('Error updating supplier:', err);
        this.errorMessage = 'Failed to update supplier. Please try again.';
        this.cdr.detectChanges();
      },
    });
  }

  removeSupplier(supplier: Supplier): void {
    if (!supplier.id) {
      this.toast.showError('Cannot delete supplier: ID is missing.');
      return;
    }

    this.supplierService.deleteSupplier(supplier.id).subscribe({
      next: () => {
        console.log('Supplier deleted successfully:', supplier.id);
        this.toast.showSuccess('Supplier deleted successfully');
        this.loadAllSuppliers();
      },
      error: (err) => {
        console.error('Error deleting supplier:', err);
        this.toast.showError('Failed to delete supplier. Please try again.');
        this.cdr.detectChanges();
      },
    });
  }
}
