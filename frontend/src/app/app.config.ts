import {
  ApplicationConfig,
  LOCALE_ID,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideIcons, provideNgIconsConfig } from '@ng-icons/core';
import { routes } from './app.routes';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { JWT_OPTIONS, JwtHelperService } from '@auth0/angular-jwt';
import {
  lucideAlertTriangle,
  lucideBriefcaseBusiness,
  lucideBuilding,
  lucideBuilding2,
  lucideCalendar,
  lucideCheckCircle2,
  lucideChevronDown,
  lucideCog,
  lucideDownload,
  lucideEye,
  lucideFile,
  lucideFileBox,
  lucideFileCheck,
  lucideFileText,
  lucideFileXCorner,
  lucideFilter,
  lucideInfo,
  lucideLogOut,
  lucideMail,
  lucidePhone,
  lucidePieChart,
  lucidePlus,
  lucideReceiptEuro,
  lucideRefreshCcw,
  lucideScale,
  lucideSearch,
  lucideTrash,
  lucideTruck,
  lucideUpload,
  lucideUser,
  lucideUserCircle,
  lucideUserCog,
  lucideUsers,
  lucideX,
} from '@ng-icons/lucide';
import { JwtHeadersInterceptor } from './core/interceptors/jwt-headers.interceptor';

import { provideCharts } from 'ng2-charts';
import {
  ArcElement,
  BarController,
  BarElement,
  CategoryScale,
  Colors,
  Legend,
  LinearScale,
  PieController,
  Tooltip,
} from 'chart.js';

import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { registerLocaleData } from '@angular/common';
import localeEl from '@angular/common/locales/el';
import { provideAnimations } from '@angular/platform-browser/animations';

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

    // Icons
    provideIcons({
      lucideTruck,
      lucideUsers,
      lucideUserCog,
      lucidePieChart,
      lucideBuilding,
      lucideUserCircle,
      lucideCog,
      lucideLogOut,
      lucideSearch,
      lucideFilter,
      lucideChevronDown,
      lucideRefreshCcw,
      lucidePlus,
      lucideTrash,
      lucideInfo,
      lucideAlertTriangle,
      lucideX,
      lucideUser,
      lucideMail,
      lucidePhone,
      lucideCalendar,
      lucideFileText,
      lucideDownload,
      lucideFileBox,
      lucideEye,
      lucideScale,
      lucideUpload,
      lucideCheckCircle2,
      lucideFileCheck,
      lucideBuilding2,
      lucideReceiptEuro,
      lucideFile,
      lucideFileXCorner,
      lucideBriefcaseBusiness,
    }),

    provideNgIconsConfig({
      size: '1.5rem',
      color: 'currentColor',
    }),

    // Charts
    provideCharts({
      registerables: [
        BarController,
        BarElement,
        PieController,
        ArcElement,
        CategoryScale,
        LinearScale,
        Legend,
        Colors,
        Tooltip,
      ],
    }),

    // Translate
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideTranslateService({
      loader: provideTranslateHttpLoader({
        prefix: './i18n/',
        suffix: '.json',
      }),
    }),

    provideAnimations(),
  ],
};
