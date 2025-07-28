import {Routes} from '@angular/router';
import {UserComponent} from "./component/admin/user/user.component";
import {LoginComponent} from "./component/auth/login/login.component";

export const routes: Routes = [
  {path: '', redirectTo: 'login', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'users', component: UserComponent},
];
