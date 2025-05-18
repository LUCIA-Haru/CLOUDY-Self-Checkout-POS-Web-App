import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import {
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { OtpVerificationComponent } from 'app/customer/Components/otp-verficiation/otp-verficiation.component';
import { AuthService } from 'app/Service/auth/auth.service';
import { ToastService } from 'app/Service/toast/toast.service';
import { ResetPassComponent } from '../reset-pass/reset-pass.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; // Add this import

@Component({
  selector: 'app-forgot-pass',
  imports: [
    MatDialogModule,
    FormsModule,
    CommonModule,
    MatCardModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
  ],
  standalone: true,
  templateUrl: './forgot-pass.component.html',
  styleUrls: ['./forgot-pass.component.css'],
})
export class ForgotPassComponent {
  forgotPasswordForm!: FormGroup;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private dialog: MatDialog,
    private ref: MatDialogRef<ForgotPassComponent>,
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  get email() {
    return this.forgotPasswordForm.get('email');
  }

  onSubmit(): void {
    if (this.forgotPasswordForm.valid) {
      this.isLoading = true;
      const { email } = this.forgotPasswordForm.value;
      this.authService.forgotPass(email).subscribe({
        next: (response) => {
          if (response.status === 'success') {
            this.toastService.showSuccess('OTP sent to your email!');
            this.ref.close();
            this.openOtpDialog(email);
          } else {
            this.toastService.showError(
              'Failed to send OTP. Please try again.',
            );
          }
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Forgot Password Failed:', error);
          this.toastService.showError('An error occurred. Please try again.');
          this.isLoading = false;
        },
      });
    } else {
      this.forgotPasswordForm.markAllAsTouched();
    }
  }

  private openOtpDialog(email: string): void {
    const dialogRef = this.dialog.open(OtpVerificationComponent, {
      width: '400px',
      height: 'auto',
      data: { email },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('OTP form modal closed', result);
      if (result?.status === 'verified') {
        this.openResetPassDialog(result.email);
      }
    });
  }

  private openResetPassDialog(email: string): void {
    const dialogRef = this.dialog.open(ResetPassComponent, {
      width: '400px',
      height: 'auto',
      data: { email },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'reset') {
        this.dialog.closeAll();
      }
    });
  }
}
