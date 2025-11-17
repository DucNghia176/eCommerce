import {bootstrapApplication} from '@angular/platform-browser';
import {AppComponent} from './app/app.component';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {appConfig} from "./app/app.config";

registerLocaleData(en);

bootstrapApplication(AppComponent, appConfig).catch(err => console.error(err));
