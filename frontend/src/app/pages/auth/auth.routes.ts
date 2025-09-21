import {Routes} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {RegisterComponent} from "./register/register.component";
import {ForgotPasswordComponent} from "./forgot-password/forgot-password.component";
import {ConfirmEmailComponent} from "./confirm-email/confirm-email.component";
import {LoginSuccessComponent} from "./login-success/login-success.component";

export const authRoutes: Routes = [{
  path: '',
  canActivate: [],
  children: [
    {path: 'login', component: LoginComponent},
    {path: 'login-success', component: LoginSuccessComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'forgot', component: ForgotPasswordComponent},
    {path: 'confirm', component: ConfirmEmailComponent},
    {path: '**', redirectTo: 'login'}
  ]
}];
