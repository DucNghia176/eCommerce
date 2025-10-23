import {Routes} from '@angular/router';
import {adminGuard} from "./guard/admin.guard";

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'user/'
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadChildren: () =>
      import('./pages/admin/admin.routes').then(m => m.adminRoutes)
  },
  {
    path: 'user',
    loadChildren: () =>
      import('./pages/user/user.routes').then(m => m.userRoutes)
  },
  {
    path: 'auth',
    loadChildren: () =>
      import('./pages/auth/auth.routes').then(m => m.authRoutes)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
