import { CanActivateChildFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { CompanyProfileService } from '../../features/company-profile/services/company-profile.service';
import { catchError, map, of } from 'rxjs';

export const companyProfileSetupGuard: CanActivateChildFn = (childRoute, state) => {
  const companyProfileService = inject(CompanyProfileService);
  const router = inject(Router);

  return companyProfileService.fetchCompanyProfile().pipe(
    map(() => true),
    catchError((err) => {
      if (err.status === 404) return of(router.createUrlTree(['/setup']));
      return of(router.createUrlTree(['/login']));
    }),
  );
};
