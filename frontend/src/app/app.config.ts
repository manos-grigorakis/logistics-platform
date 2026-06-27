import {
  ApplicationConfig,
  LOCALE_ID,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { JWT_OPTIONS, JwtHelperService } from '@auth0/angular-jwt';
import { JwtHeadersInterceptor } from './core/interceptors/jwt-headers.interceptor';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { registerLocaleData } from '@angular/common';
import localeEl from '@angular/common/locales/el';
import { provideAnimations } from '@angular/platform-browser/animations';
import { appIconsConfig, appIconsProvider } from './core/config/app-icons.config';
import { appChartsProvider } from './core/config/app-charts.config';

registerLocaleData(localeEl);
export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: JwtHeadersInterceptor, multi: true },
    { provide: JWT_OPTIONS, useValue: JWT_OPTIONS },
    JwtHelperService,
    { provide: LOCALE_ID, useValue: 'el_GR' },

    // Translate
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideTranslateService({
      loader: provideTranslateHttpLoader({
        prefix: './i18n/',
        suffix: '.json',
      }),
    }),

    // Icons
    appIconsConfig,
    appIconsProvider,

    // Charts
    appChartsProvider,

    // Required for color picker
    provideAnimations(),
  ],
};
