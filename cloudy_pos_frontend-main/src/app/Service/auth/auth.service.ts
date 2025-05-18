import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, throwError } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import { decodeToken, isTokenExpired } from 'app/utils/jwt.utils';
import { ToastService } from 'app/Service/toast/toast.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  public isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();
  defaultAvatar = 'assets/default-avatar.png';

  // New BehaviorsSubject to store our data
  private userSubject = new BehaviorSubject<any>(null);
  user$ = this.userSubject.asObservable();

  private apiUrl = '/user';
  constructor(
    private http: HttpClient,
    private cookieService: CookieService,
    private toastService: ToastService,
  ) {
    // Check if token exists on app initialization
    const token = this.getToken();
    if (token && !isTokenExpired(token)) {
      this.isLoggedInSubject.next(true);
      this.setUserFromToken(token);
    }
  }

  login(username: string, password: string): Observable<any> {
    return this.http
      .post(
        `${this.apiUrl}/login
        `,
        { username, password },
        { withCredentials: true },
      )
      .pipe(
        tap((response: any) => {
          const token = response.data.token; // Assuming backend returns { token: 'jwt-token' }
          if (token) {
            // console.log('Token received from backend:', token);
            this.setTokenInCookie(token); // Store token securely in a cookie
            this.setUserFromToken(token);
            this.isLoggedInSubject.next(true);
          } else {
            // console.error('No token found in response');
            this.toastService.showError('Invalid username or password');
          }
        }),
      );
  }

  getToken(): string | null {
    return this.cookieService.get('token'); // Retrieve token from cookies
  }
  logout() {
    this.cookieService.delete('token', '/'); // Remove token from cookies
    this.isLoggedInSubject.next(false);
    sessionStorage.clear(); // Clear session storage
    this.cookieService.delete('role', '/'); // Remove role cookie
    this.cookieService.delete('username', '/'); // Remove username cookie
    this.userSubject.next(null); // Clear user data
  }
  isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token && !isTokenExpired(token);
  }
  getUserName(): string {
    const token = this.getToken();
    if (!token) return 'Guest';

    const decodedToken = decodeToken(token);
    return decodedToken?.sub || 'Guest'; // Extract name from token claims
  }
  getRoles(): string {
    const token = this.getToken();
    if (!token) return 'guest';

    const decodedToken = decodeToken(token);
    return decodedToken?.role || 'guest'; // Extract roles from token claims
  }
  setRoles(roles: string[]): void {
    this.cookieService.set('role', JSON.stringify(roles));
  }
  private setTokenInCookie(token: string): void {
    this.cookieService.set(
      'token', // Name of the cookie
      token, // Value of the cookie (JWT token)
      1, // Expiration time in days
      '/', // Path (accessible across the entire app)
      undefined, // Domain (optional)
      false, // Secure flag (only send over HTTPS)
      'Lax', // SameSite policy ('Strict', 'Lax', or 'None')
    );
  }
  // Method to extract user data from token and update userSubject
  public setUserFromToken(token: string): void {
    const decodedToken = decodeToken(token);
    const userData = {
      username: decodedToken?.sub || 'Guest', // Extract name from token
      avatar: decodedToken?.avatar || this.defaultAvatar, // Extract avatar URL or use default
      role: decodedToken?.role || 'user',
    };
    this.userSubject.next(userData); // Update user data
  }

  verifyOtp(otpData: any, otp: string): Observable<any> {
    const params = new HttpParams().set('otp', otp);
    return this.http
      .post(`${this.apiUrl}/verify-otp`, otpData, { params })
      .pipe(
        tap((response: any) => {
          if (response.status === 'success') {
            // console.log('otp verified successfully');
            this.toastService.showSuccess(
              'You have been successfully registered! Please Login again',
            );
          } else {
            this.toastService.showError('Invalid OTP');
          }
        }),
      );
  }

  // pass
  forgotPass(email: string): Observable<any> {
    const params = new HttpParams().set('email', email);
    return this.http.put(`${this.apiUrl}/forgotPassword`, {}, { params });
  }

  resetPassword(email: string, password: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/resetPassword`, null, {
      params: { email, password },
    });
  }
  verifyForgotPassOtp(email: string, otp: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/verify-otp-pass`, null, {
      params: { email, otp },
    });
  }

  changePass(
    id: number,
    oldPassword: string,
    newPassword: string,
  ): Observable<any> {
    return this.http.put(`${this.apiUrl}/changePassword/${id}`, null, {
      params: { oldPassword, newPassword },
    });
  }
}
