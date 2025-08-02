import {bootstrapApplication} from '@angular/platform-browser';
import {AppComponent} from './app/app.component';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {AuthInterceptor} from './app/core/interceptor/auth.interceptor';
import {routes} from "./app/app.routes";
import {provideRouter} from "@angular/router";

// bootstrapApplication(AppComponent, appConfig)
//   .catch((err) => console.error(err));

bootstrapApplication(AppComponent, {
  providers: [provideHttpClient(withInterceptors([AuthInterceptor])),
    provideRouter(routes),
  ]
}).catch(err => console.error(err));
