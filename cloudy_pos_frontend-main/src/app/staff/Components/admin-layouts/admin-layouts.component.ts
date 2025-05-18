import { Component } from '@angular/core';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { RouterOutlet } from '@angular/router';
import { AuthService } from 'app/Service/auth/auth.service';

@Component({
  selector: 'app-admin-layouts',
  imports: [SidebarComponent, RouterOutlet],
  templateUrl: './admin-layouts.component.html',
  styleUrl: './admin-layouts.component.css',
})
export class AdminLayoutsComponent {
  // constructor(private authService: AuthService) {}
  // role: string[] | undefined;
  // isAdmin: boolean | undefined;
  // sidebarRole: string = 'guest'; // Default role
  // ngOnInit(): void {
  //   this.role = this.authService.getRoles();
  //   this.isAdmin = this.role?.includes('admin') || false;
  //   // Determine the sidebar role based on the user's roles
  //   if (this.isAdmin) {
  //     this.sidebarRole = 'admin';
  //   } else if (this.role?.includes('user')) {
  //     this.sidebarRole = 'user';
  //   } else {
  //     this.sidebarRole = 'guest';
  //   }
  // }
}
