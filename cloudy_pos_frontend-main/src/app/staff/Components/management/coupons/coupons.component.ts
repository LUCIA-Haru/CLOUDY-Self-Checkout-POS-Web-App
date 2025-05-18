import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { TableComponent } from 'app/Common/components/table/table.component';
import { AssignDialogComponent } from '../../managment/assign-dialog/assign-dialog.component';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { CouponsService } from 'app/staff/service/coupons/coupons.service';
import { ToastService } from 'app/Service/toast/toast.service';

@Component({
  selector: 'app-coupons',
  imports: [
    TableComponent,
    CommonModule,
    FormsModule,
    MatDatepickerModule,
    MatInputModule,
    MatNativeDateModule,
  ],
  templateUrl: './coupons.component.html',
  styleUrl: './coupons.component.css',
})
export class CouponsComponent implements OnInit {
  coupons: any[] = [];
  currentPage: number = 1;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 1;
  filterValue = '';
  columns: any[] = [
    {
      key: 'couponCode',
      label: 'Coupon Code',
      sortable: true,
      editable: false,
    },
    { key: 'discountAmount', label: 'Discount Amount', sortable: true },
    { key: 'minPurchaseAmount', label: 'Min Purchase', sortable: true },
    {
      key: 'expirationDate',
      label: 'Expiration Date',
      type: 'date',
      sortable: true,
    },
    {
      key: 'assignedTo',
      label: 'Assigned To',
      sortable: true,
      editable: false,
    },
    {
      key: 'active',
      label: 'Status',
      sortable: true,
      type: 'select',
      options: [
        { value: 'true', label: 'Active' },
        { value: 'false', label: 'Inactive' },
      ],
    },
  ];
  filterConfig = {
    column: 'active',
    options: [
      { value: 'all', label: 'All' },
      { value: 'true', label: 'Active' },
      { value: 'false', label: 'Inactive' },
    ],
  };

  constructor(
    private couponsService: CouponsService,
    private dialog: MatDialog,
    private toastr: ToastService,
  ) {}

  ngOnInit() {
    this.fetchCoupons();
  }

  fetchCoupons() {
    this.couponsService.fetchCoupons().subscribe({
      next: (response: any) => {
        this.coupons = response.data.assignedCoupons;
      },
      error: (err: any) => {
        this.toastr.showError('Failed to load coupons');
        console.error(err);
      },
    });
  }

  openAssignDialog() {
    this.couponsService.fetchCoupons().subscribe({
      next: (response: any) => {
        const dialogRef = this.dialog.open(AssignDialogComponent, {
          width: '600px',
          data: { coupons: response.data.unassignedCoupons },
        });

        dialogRef.afterClosed().subscribe((result: any) => {
          if (result) {
            this.fetchCoupons(); // Refresh the coupon list
          }
        });
      },
      error: (err: any) => {
        this.toastr.showError('Failed to load coupons for assignment');
        console.error(err);
      },
    });
  }

  generateCoupon(coupon: any): void {
    this.couponsService.generateCoupons(coupon).subscribe({
      next: (response) => {
        this.toastr.showSuccess('Generate Coupon Successfully');
        this.fetchCoupons();
      },
      error: (error) => {
        this.toastr.showError('Something went wrong');
        console.error(error);
      },
    });
  }
  updateCoupon(coupons: any) {
    this.couponsService.updateCoupon(coupons, coupons.couponId).subscribe({
      next: (response) => {
        this.toastr.showSuccess('Update Coupon Successfully');
        this.fetchCoupons();
      },
      error: (error) => {
        this.toastr.showError('Something went wrong');
        console.error(error);
      },
    });
  }
  deleteCoupon(coupon: any) {
    this.couponsService.deleteCoupon(coupon.couponId).subscribe({
      next: (response) => {
        this.toastr.showSuccess('Delete Coupon Successfully');
        this.fetchCoupons();
      },
      error: (error) => {
        this.toastr.showError('Something went wrong');
        console.error(error);
      },
    });
  }
  onFilterChange(filterValue: string) {
    this.filterValue = filterValue;
    this.currentPage = 1;
    this.fetchCoupons();
  }
  onPageChange(page: number) {
    this.currentPage = page;
    this.fetchCoupons();
  }
}
