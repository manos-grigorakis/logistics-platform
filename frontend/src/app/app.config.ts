import {
  ApplicationConfig,
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
} from '@ng-icons/lucide';
import { JwtHeadersInterceptor } from './auth/interceptors/jwt-headers.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: JwtHeadersInterceptor, multi: true },
    { provide: JWT_OPTIONS, useValue: JWT_OPTIONS },
    JwtHelperService,

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
    }),

    provideNgIconsConfig({
      size: '1.5rem',
      color: 'currentColor',
    }),
  ],
};
