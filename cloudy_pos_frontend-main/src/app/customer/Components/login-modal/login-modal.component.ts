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
import { RegisterModalComponent } from '../register-modal/register-modal.component';
import { ToastService } from 'app/Service/toast/toast.service';
import { ForgotPassComponent } from 'app/Common/dialog/forgot-pass/forgot-pass.component';

// Correct path and interface name

@Component({
  standalone: true,
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
  templateUrl: './login-modal.component.html',
  styleUrls: ['./login-modal.component.css'],
})
export class LoginModalComponent implements OnInit {
  hide = true;
  loginForm: any;
  title = 'Login';
  constructor(
    private ref: MatDialogRef<LoginModalComponent>,
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private dialog: MatDialog,
    private toastService: ToastService,
  ) {}
  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  // Login
  loginSubmit() {
    if (this.loginForm.valid) {
      const loginData: Login = this.loginForm.value;
      // Call the service method to handle login
      this.authService.login(loginData.username, loginData.password).subscribe({
        next: (response: any) => {
          // console.log('Login successful:', response);
          this.toastService.showSuccess('Login successful');
          this.ref.close();
        },
        error: (error: any) => {
          let errorMessage = 'An unknown error occurred.';
          if (error.error instanceof ErrorEvent) {
            errorMessage = `Client-side error: ${error.error.message}`;
          } else {
            // Backend error
            errorMessage = error.error?.message || error.statusText;
            // Optionally modify the error message
            if (errorMessage === 'Invalid username or password.') {
              errorMessage =
                'The username or password you entered is incorrect. Please try again.';
            }
          }
          this.toastService.showError(errorMessage); // Show error toast
        },
      });
    }
  }
  closeModel() {
    this.ref.close();
  }

  openRegisterModal() {
    // Close the login dialog
    this.ref.close();
    // Open the Register dialog
    this.dialog.open(RegisterModalComponent, {
      width: '50%',
      height: 'auto',
      exitAnimationDuration: '1000ms',
      enterAnimationDuration: '1000ms',
    });
  }

  openForgotModel() {
    this.ref.close();
    this.dialog.open(ForgotPassComponent, {
      width: '50%',
      height: 'auto',
      exitAnimationDuration: '1000ms',
      enterAnimationDuration: '1000ms',
    });
  }
  // Getter methods for form controls
  get username() {
    return this.loginForm.get('username');
  }

  get password() {
    return this.loginForm.get('password');
  }
}
