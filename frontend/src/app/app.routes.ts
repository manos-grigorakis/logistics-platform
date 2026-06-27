import { Routes } from '@angular/router';
import { LoginForm } from './core/auth/login-form/login-form';
import { authGuard } from './core/guards/auth-guard';
import { MainLayout } from './core/layout/main-layout/main-layout';
import { MainDashboard } from './features/dashboard/pages/main-dashboard/main-dashboard';
import { roleGuard } from './core/guards/role-guard';
import { companyProfileSetupGuard } from './core/guards/company-profile-setup-guard';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    title: 'Logistics Platform',
    canActivate: [authGuard],
    canActivateChild: [companyProfileSetupGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: MainDashboard, title: 'Dashboard' },
      {
        path: 'users',
        canActivate: [roleGuard],
        loadChildren: () => import('./features/users/users.routes'),
      },
      {
        path: 'roles',
        canActivate: [roleGuard],
        loadChildren: () => import('./features/roles/roles.routes'),
      },
      {
        path: 'customers',
        loadChildren: () => import('./features/customers/routes/customers.routes'),
      },
      {
        path: 'quotes',
        loadChildren: () => import('./features/quotes/quotes.routes'),
      },
      {
        path: 'vehicles',
        loadChildren: () => import('./features/vehicles/vehicles.routes'),
      },
      {
        path: 'shipments',
        loadChildren: () => import('./features/shipments/shipments.routes'),
      },
      {
        path: 'payments-tracking',
        loadComponent: () =>
          import('./features/payments/payments-page/payments-page').then((m) => m.PaymentsPage),
        title: 'Payments Tracking',
      },
      {
        path: 'cmr-documents',
        loadChildren: () => import('./features/cmr-documents/cmr-documents.routes'),
      },
      {
        path: 'suppliers',
        loadChildren: () => import('./features/suppliers/routes/suppliers.routes'),
      },
      {
        path: 'company-profile',
        children: [
          {
            path: 'edit',
            loadComponent: () =>
              import(
                './features/company-profile/pages/edit-company-profile/edit-company-profile'
              ).then((m) => m.EditCompanyProfile),
            title: 'Edit Company Profile',
          },
        ],
      },
    ],
  },
  {
    path: 'setup',
    loadComponent: () =>
      import('./features/company-profile/pages/setup-company-profile/setup-company-profile').then(
        (m) => m.SetupCompanyProfile,
      ),
    title: 'Setup Company Profile',
    canActivate: [authGuard, roleGuard],
  },
  {
    path: 'login',
    component: LoginForm,
    title: 'Login',
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./core/auth/forgot-password-form/forgot-password-form').then(
        (m) => m.ForgotPasswordForm,
      ),
    title: 'Forgot Password',
  },
  {
    path: 'reset-password',
    loadComponent: () =>
      import('./core/auth/reset-password-form/reset-password-form').then(
        (m) => m.ResetPasswordForm,
      ),
    title: 'Reset Password',
  },
  {
    path: 'setup-password',
    loadComponent: () =>
      import('./core/auth/setup-password-form/setup-password-form').then(
        (m) => m.SetupPasswordForm,
      ),
    title: 'Setup Password',
  },
  {
    path: 'forbidden',
    loadComponent: () => import('./pages/forbidden/forbidden').then((m) => m.Forbidden),
    title: '403 Forbidden',
  },
  {
    path: '**',
    loadComponent: () => import('./pages/not-found/not-found').then((m) => m.NotFound),
    title: '404 Not Found',
  },
];
