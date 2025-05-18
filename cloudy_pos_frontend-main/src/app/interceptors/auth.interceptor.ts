import { HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from 'app/Service/auth/auth.service';
import { Router } from '@angular/router';
import { isTokenExpired } from 'app/utils/jwt.utils';
import { ToastService } from 'app/Service/toast/toast.service';

// auth.interceptor.ts
export const authInterceptor = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const toastService = inject(ToastService);

  const publicEndpoints = [
    '/user/login',
    '/user/signup',
    '/product/search/',
    '/purchase/payment',
  ];

  if (publicEndpoints.some((endpoint) => req.url.includes(endpoint))) {
    return next(req);
  }

  const token = authService.getToken();
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401 && token) {
        console.warn(`401 for ${req.url}. Token: ${token.substring(0, 10)}...`);
        if (isTokenExpired(token)) {
          console.error('Token expired. Logging out...');
          toastService.showError('Token expired. Logging out...');
          authService.logout();
          router.navigate(['/login'], { queryParams: { returnUrl: req.url } });
        } else {
          console.log('Token valid; server rejected request. Not logging out.');
          // toastService.showWarning(
          //   'Token valid; server rejected request. Not logging out.',
          // );
        }
      }
      return throwError(() => error);
    }),
  );
};
