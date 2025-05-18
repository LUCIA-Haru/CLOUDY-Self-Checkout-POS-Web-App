import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withHashLocation } from '@angular/router';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideToastr } from 'ngx-toastr';
import { authInterceptor } from 'app/interceptors/auth.interceptor';
import { CookieService } from 'ngx-cookie-service';
import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withHashLocation()), // Enable Hash-Based Routing ✅
    provideAnimationsAsync(),
    provideToastr(),
    provideHttpClient(withInterceptors([authInterceptor])),
    CookieService,
  ],
};
