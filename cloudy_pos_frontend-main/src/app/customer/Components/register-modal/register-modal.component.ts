import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import {
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import {
  AbstractControlOptions,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from 'app/Service/auth/auth.service';
import { Login } from 'app/customer/Model/login';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { LoginModalComponent } from '../login-modal/login-modal.component';
import { OtpVerificationComponent } from '../otp-verficiation/otp-verficiation.component';
import { ToastService } from 'app/Service/toast/toast.service';

@Component({
  selector: 'app-register-modal',
  imports: [
    MatCardModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatSelectModule,
    MatIconModule,
    CommonModule,
    MatDialogModule,
  ],
  templateUrl: './register-modal.component.html',
  styleUrls: ['./register-modal.component.css'],
})
export class RegisterModalComponent implements OnInit {
  hide = true;
  regForm: any;
  title = 'Register';
  hideConfirm = true;

  apiUrl = '/user/signup';
  constructor(
    private ref: MatDialogRef<RegisterModalComponent>,
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private http: HttpClient,
    private dialog: MatDialog,
    private toastService: ToastService,
  ) {}
  ngOnInit(): void {
    this.initForm();
  }

  /**
   * Initializes the registration form with validation rules.
   */
  private initForm(): void {
    this.regForm = this.fb.group(
      {
        username: [
          '',
          {
            validators: [
              Validators.required,
              Validators.pattern('^[A-Z][A-Za-z!@#$%^&*(),.?":{}|<>0-9]{2,}$'),
              Validators.minLength(3),
              Validators.maxLength(256),
            ],
          },
        ],
        email: ['', { validators: [Validators.required, Validators.email] }],
        password: [
          '',
          {
            validators: [
              Validators.required,
              Validators.minLength(6),
              Validators.maxLength(32),
              Validators.pattern(
                '^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{}\\[\\]|:;"\'<>,.?/~`]).{6,}$',
              ),
            ],
          },
        ],
        confirmPassword: ['', { validators: [Validators.required] }],
      },
      { validators: this.passwordMatchValidator } as AbstractControlOptions, // Explicitly specify type
    );

    this.regForm.get('password')?.valueChanges.subscribe(() => {
      this.regForm.get('confirmPassword')?.updateValueAndValidity();
    });
  }

  /**
   * Custom validator to check if the password and confirmPassword fields match.
   */
  private passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');

    if (!password || !confirmPassword) {
      return { missingControls: true };
    }

    if (password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ mismatch: true });
    }

    return null;
  }

  /**
   * Handles form submission and sends data to the backend.
   */
  onRegister(): void {
    if (this.regForm.valid) {
      const formData = {
        username: this.regForm.value.username,
        email: this.regForm.value.email,
        password: this.regForm.value.password,
      };

      this.http.post(this.apiUrl, formData).subscribe({
        next: (response) => {
          console.log('Registration Successful', response);
          this.toastService.showSuccess(
            'Registration successful! Please verify your email.',
          );

          this.ref.close();
          // Open OTP form modal
          this.openOtpFormModal();
          this.regForm.reset();
          // this.router.navigate(['home']);
        },
        error: (error) => {
          console.error('Registration failed', error);
          this.toastService.showError('Registration failed. Please try again.');
        },
      });
    } else {
      this.regForm.markAllAsTouched(); // Shows errors if fields are invalid
    }
  }

  // Getter methods for form controls
  get username() {
    return this.regForm.get('username');
  }

  get email() {
    return this.regForm.get('email');
  }

  get password() {
    return this.regForm.get('password');
  }

  get confirmPassword() {
    return this.regForm.get('confirmPassword');
  }
  openLoginModel() {
    this.ref.close();
    // Open the login model
    this.dialog.open(LoginModalComponent, {
      width: '50%',
      height: 'auto', // or a specific height
      maxHeight: '80vh',
      exitAnimationDuration: '1000ms',
      enterAnimationDuration: '1000ms',
    });
  }
  openOtpFormModal(): void {
    const dialogRef = this.dialog.open(OtpVerificationComponent, {
      width: '50%', // Set the width of the modal
      height: 'auto', // or a specific height
      maxHeight: '80vh',
      exitAnimationDuration: '1000ms',
      enterAnimationDuration: '1000ms',
      data: {
        username: this.regForm.value.username, // Pass the username
        email: this.regForm.value.email, // Pass the email
        password: this.regForm.value.password, // Pass the password
      },
    });

    // Handle actions after the OTP form modal is closed
    dialogRef.afterClosed().subscribe((result) => {
      console.log('OTP form modal closed', result);
      if (result === 'verified') {
        this.router.navigate(['home']);
      } else {
        // console.log('OTP verification failed or canceled');
        this.toastService.showError(
          'OTP verification failed. Please try again.',
        );
      }
    });
  }
}
