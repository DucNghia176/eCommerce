import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from "../core/services/auth.service";
import {inject} from "@angular/core";
import {Role} from "../shared/status/role";

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();
  if (!token) {
    router.navigate(['/auth/login']);
    return false;
  }
  const payload = JSON.parse(atob(token.split('.')[1]));
  const roles: string[] = payload.role;
  if (roles.includes(Role.admin)) {
    return true;
  }
  window.location.href = '/user';
  return false;
};
