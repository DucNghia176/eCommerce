import {Routes} from '@angular/router';
import {UserAdminComponent} from "../admin/components/user/user.component";

export const userRoutes: Routes = [{
  path: '',
  canActivate: [],
  children: [
    {path: 'userAdmin', component: UserAdminComponent},
    {path: '**', redirectTo: ''}
  ]
}];
