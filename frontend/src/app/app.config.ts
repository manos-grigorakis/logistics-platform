import {
  ApplicationConfig,
  inject,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideIcons, provideNgIconsConfig } from '@ng-icons/core';
import { routes } from './app.routes';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { JWT_OPTIONS, JwtHelperService } from '@auth0/angular-jwt';

// Lucide Icons
import {
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
} from '@ng-icons/lucide';
import { JwtHeadersInterceptor } from './auth/interceptors/jwt-headers.interceptor';

import { provideCharts } from 'ng2-charts';
import {
  BarController,
  PieController,
  ArcElement,
  CategoryScale,
  LinearScale,
  Legend,
  Colors,
  Tooltip,
  BarElement,
} from 'chart.js';

import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: JwtHeadersInterceptor, multi: true },
    { provide: JWT_OPTIONS, useValue: JWT_OPTIONS },
    JwtHelperService,

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
  ],
};
