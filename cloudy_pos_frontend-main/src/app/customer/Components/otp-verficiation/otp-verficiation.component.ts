import { Component, Inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from 'app/Service/auth/auth.service';
import { ToastService } from 'app/Service/toast/toast.service';

@Component({
  selector: 'app-otp-verficiation',
  imports: [
    MatCardModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    CommonModule,
    MatDialogModule,
  ],
  standalone: true,
  templateUrl: './otp-verficiation.component.html',
  styleUrls: ['./otp-verficiation.component.css'],
})
export class OtpVerificationComponent implements OnInit {
  otpForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private ref: MatDialogRef<OtpVerificationComponent>,
    private toastService: ToastService,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      username?: string;
      email: string;
      password?: string;
    },
  ) {}

  ngOnInit(): void {
    this.initForm();
    console.log('Data received in OTP modal:', this.data);
  }

  private initForm(): void {
    this.otpForm = this.fb.group({
      otp: ['', [Validators.required, Validators.pattern('^[0-9]{4}$')]],
    });
  }

  get otp() {
    return this.otpForm.get('otp');
  }

  onVerifyOtp(): void {
    if (this.otpForm.valid) {
      const otp = this.otpForm.value.otp;
      // Check if username and password are provided (registration flow)
      if (this.data.username && this.data.password) {
        const otpData = {
          username: this.data.username,
          email: this.data.email,
          password: this.data.password,
          otp,
        };
        this.authService.verifyOtp(otpData, otp).subscribe({
          next: (response) => {
            if (response.status === 'success') {
              this.toastService.showSuccess('OTP Verified Successfully!');
              this.ref.close('verified');
            } else {
              this.toastService.showError('Invalid OTP. Please try again.');
            }
          },
          error: (error) => {
            console.error('OTP Verification Failed:', error);
            this.toastService.showError(
              'An error occurred while verifying OTP.',
            );
          },
        });
      } else {
        // Forgot password flow (only email provided)
        this.authService.verifyForgotPassOtp(this.data.email, otp).subscribe({
          next: (response: any) => {
            if (response.status === 'success') {
              this.toastService.showSuccess('OTP Verified Successfully!');
              this.ref.close({ status: 'verified', email: this.data.email });
            } else {
              this.toastService.showError('Invalid OTP. Please try again.');
            }
          },
          error: (error: any) => {
            console.error('OTP Verification Failed:', error);
            this.toastService.showError(
              'An error occurred while verifying OTP.',
            );
          },
        });
      }
    } else {
      this.otpForm.markAllAsTouched();
    }
  }
}
