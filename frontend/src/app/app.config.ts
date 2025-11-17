import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';

import {routes} from './app.routes';
import {AuthInterceptor} from './core/interceptor/auth.interceptor';
import {ngrokInterceptor} from './core/interceptor/ngrok.interceptor';

import {BrowserAnimationsModule, provideAnimations} from '@angular/platform-browser/animations';
import {provideToastr} from 'ngx-toastr';
import {en_US, provideNzI18n} from 'ng-zorro-antd/i18n';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {FormsModule} from '@angular/forms';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {NzModalModule} from 'ng-zorro-antd/modal';

registerLocaleData(en);

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),

    // Gộp Interceptor: Auth + Ngrok
    provideHttpClient(withInterceptors([
      AuthInterceptor,
      ngrokInterceptor
    ])),

    provideAnimations(),
    provideToastr({
      timeOut: 3000,
      positionClass: 'toast-bottom-right',
      preventDuplicates: true,
      progressBar: true
    }),
    provideNzI18n(en_US),

    importProvidersFrom(
      FormsModule,
      BrowserAnimationsModule,
      NzModalModule
    ),

    provideAnimationsAsync()
  ]
};
