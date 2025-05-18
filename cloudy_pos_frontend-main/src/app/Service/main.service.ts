import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root',
})
export class MainService {
  constructor(private cookieService: CookieService) {}
  isUserLoggedIn(): boolean {
    const authToken = this.cookieService.get('token');
    return !!authToken; // Returns true if the token exists, false otherwise
  }
}
