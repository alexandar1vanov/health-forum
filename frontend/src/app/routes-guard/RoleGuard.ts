import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from '../services/AuthService';

export const RoleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAdmin()) {
    router.navigate(['/unauthorized']);
    return false;
  }
  return true;
};
