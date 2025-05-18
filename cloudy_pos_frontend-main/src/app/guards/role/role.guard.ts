import { CanActivateFn, Router } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { AuthService } from 'app/Service/auth/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService); // Inject AuthService
  const router = inject(Router); // Inject Router

  const requiredRoles = route.data?.['role'] ?? []; // Roles required for this route
  const userRole: string = authService.getRoles(); // Now returns a single string
  const hasRole =
    requiredRoles.length === 0 || requiredRoles.includes(userRole);

  if (!hasRole) {
    console.error('Unauthorized access. Redirecting...');
    router.navigate(['/unauthorized']); // Redirect to unauthorized page
    return false;
  }
  console.log('User Roles (processed):', userRole);
  console.log('Required Roles (processed):', requiredRoles);
  console.log('Has Role:', hasRole);

  return true; // Allow access to the route
};
