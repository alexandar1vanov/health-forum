import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from '../services/AuthService';

export const AuthGuard : CanActivateFn = () => {
  const authService=  inject(AuthService)
  const router = inject(Router)
  if (!authService.getToken()){
    router.navigate(['/login'])
    return false
  }
  return true
}
