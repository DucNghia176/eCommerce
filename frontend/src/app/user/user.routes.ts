import {Routes} from '@angular/router';
import {HomeComponent} from "./pages/home/home.component";

export const userRoutes: Routes = [{
  path: '',
  canActivate: [],
  children: [
    {path: '', component: HomeComponent},
    {path: '**', redirectTo: '', pathMatch: 'full'}
  ]
}];
