import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CouponsService } from 'app/staff/service/coupons/coupons.service';
import { ToastrService } from 'ngx-toastr';

interface Customer {
  user_id: number;
  username: string;
}

interface Coupon {
  couponId: number;
  couponCode: string;
}
@Component({
  selector: 'app-assign-dialog',
  imports: [
    CommonModule,
    MatDialogModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  templateUrl: './assign-dialog.component.html',
  styleUrl: './assign-dialog.component.css',
})
export class AssignDialogComponent {
  assignForm: FormGroup;
  customers: Customer[] = [];
  coupons: Coupon[] = [];
  loading = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AssignDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { coupons: any[] },
    private couponsService: CouponsService,
    private toastr: ToastrService,
  ) {
    this.assignForm = this.fb.group({
      customerId: ['', Validators.required],
      couponCode: ['', Validators.required],
    });
  }

  ngOnInit() {
    // Filter unassigned and active coupons
    this.coupons = this.data.coupons
      .filter((coupon) => !coupon.assignedTo && coupon.active)
      .map((coupon) => ({
        couponId: coupon.couponId,
        couponCode: coupon.couponCode,
      }));

    // Fetch customers
    this.couponsService.fetchCustomers().subscribe({
      next: (response: any) => {
        this.customers = response.data.map((customer: any) => ({
          user_id: customer.user_id,
          username: customer.username,
        }));
      },
      error: (err: any) => {
        this.toastr.error('Failed to load customers');
        console.error(err);
      },
    });
  }

  onSave() {
    if (this.assignForm.valid) {
      this.loading = true;
      const { customerId, couponCode } = this.assignForm.value;
      this.couponsService.assignCoupon(customerId, couponCode).subscribe({
        next: (response: any) => {
          this.toastr.success('Coupon assigned successfully');
          this.dialogRef.close({ customerId, couponCode });
        },
        error: (err: any) => {
          this.toastr.error('Failed to assign coupon');
          this.loading = false;
          console.error(err);
        },
      });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
