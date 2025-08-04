import {Routes} from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'user/'
  },
  {
    path: 'admin',
    loadChildren: () =>
      import('./admin/admin.routes').then(m => m.adminRoutes)
  },
  {
    path: 'user',
    loadChildren: () =>
      import('./user/user.routes').then(m => m.userRoutes)
  },
  {
    path: 'auth',
    loadChildren: () =>
      import('./auth/auth.routes').then(m => m.authRoutes)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
