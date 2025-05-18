import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';
import { Login } from 'app/customer/Model/login';
import { AuthService } from 'app/Service/auth/auth.service';
import { ToastService } from 'app/Service/toast/toast.service';
import { roleGuard } from '../../../../guards/role/role.guard';

@Component({
  selector: 'app-login-panel',
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
  ],
  templateUrl: './login-panel.component.html',
  styleUrls: ['./login-panel.component.css'],
})
export class LoginPanelComponent implements OnInit {
  hide = true;
  loginForm: any;
  title = 'WELCOME BACK';
  isSubmitting = false; // Track form submission state

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  loginSubmit() {
    if (this.loginForm.valid && !this.isSubmitting) {
      this.isSubmitting = true; // Disable form during submission
      const loginData: Login = this.loginForm.value;

      this.authService.login(loginData.username, loginData.password).subscribe({
        next: (response: any) => {
          this.isSubmitting = false;
          this.toastService.showSuccess('Login successful');

          // Map roles to dashboard routes
          const dashboardMap: { [key: string]: string } = {
            ADMIN: '/staff/admin-dashboard',
            STAFF: '/staff/staff-dashboard',
            MANAGER: '/staff/manager-dashboard',
          };

          // Get user role from response (adjust based on your API response structure)
          const userRole = response.data.role; // e.g., 'ADMIN', 'STAFF', 'MANAGER'
          const redirectPath = dashboardMap[userRole] || '/staff/loginPanel'; // Fallback to loginPanel

          this.router.navigate([redirectPath]);
        },
        error: (error: any) => {
          this.isSubmitting = false;
          let errorMessage = 'An unknown error occurred.';
          if (error.error instanceof ErrorEvent) {
            errorMessage = `Client-side error: ${error.error.message}`;
          } else {
            errorMessage = error.error?.message || error.statusText;
            if (errorMessage === 'Invalid username or password.') {
              errorMessage =
                'The username or password you entered is incorrect. Please try again.';
            }
          }
          this.toastService.showError(errorMessage);
        },
      });
    }
  }
}
