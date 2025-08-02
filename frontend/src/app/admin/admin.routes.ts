import {Routes} from '@angular/router';
import {UserAdminComponent} from "./components/user/user.component";

export const adminRoutes: Routes = [{
  path: '',
  canActivate: [],
  children: [
    {path: 'userAdmin', component: UserAdminComponent},
    {path: '**', redirectTo: ''}
  ]
}];
