import { Component, Inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { AuthService } from 'app/Service/auth/auth.service';
import { ToastService } from 'app/Service/toast/toast.service';

@Component({
  selector: 'app-reset-pass',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatDialogModule,
  ],
  standalone: true,
  templateUrl: './reset-pass.component.html',
  styleUrls: ['./reset-pass.component.css'],
})
export class ResetPassComponent implements OnInit {
  resetPasswordForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private ref: MatDialogRef<ResetPassComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { email: string },
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.resetPasswordForm = this.fb.group(
      {
        password: this.fb.control('', {
          validators: [Validators.required, Validators.minLength(6)],
        }),
        confirmPassword: this.fb.control('', {
          validators: [Validators.required],
        }),
      },
      { validators: this.passwordMatchValidator },
    );
  }

  // Custom validator to check if passwords match
  private passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { mismatch: true };
  }

  get password() {
    return this.resetPasswordForm.get('password');
  }

  get confirmPassword() {
    return this.resetPasswordForm.get('confirmPassword');
  }

  onSubmit(): void {
    if (this.resetPasswordForm.valid) {
      const { password } = this.resetPasswordForm.value;
      this.authService.resetPassword(this.data.email, password).subscribe({
        next: (response) => {
          if (response.status === 'success') {
            this.toastService.showSuccess('Password reset successfully!');
            this.ref.close('reset');
          } else {
            this.toastService.showError(
              'Failed to reset password. Please try again.',
            );
          }
        },
        error: (error) => {
          console.error('Password Reset Failed:', error);
          this.toastService.showError(
            'An error occurred while resetting password.',
          );
        },
      });
    } else {
      this.resetPasswordForm.markAllAsTouched();
    }
  }
}
