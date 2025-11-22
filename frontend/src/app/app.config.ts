import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideIcons, provideNgIconsConfig } from '@ng-icons/core';
import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';
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
} from '@ng-icons/lucide';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
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
    }),

    provideNgIconsConfig({
      size: '1.5rem',
      color: 'currentColor',
    }),
  ],
};
