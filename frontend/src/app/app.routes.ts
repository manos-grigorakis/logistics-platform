import { Routes } from '@angular/router';
import { LoginPage } from './auth/login-page/login-page';
import { authGuard } from './guards/auth-guard';
import { NotFoundPage } from './not-found-page/not-found-page';
import { ForgotPasswordPage } from './auth/forgot-password-page/forgot-password-page';
import { ResetPasswordPage } from './auth/reset-password-page/reset-password-page';
import { SetupPasswordPage } from './auth/setup-password-page/setup-password-page';
import { MainLayout } from './layout/main-layout/main-layout';
import { MainDashboard } from './dashboard/main-dashboard/main-dashboard';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    title: 'Logistics Platform',
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: MainDashboard, title: 'Dashboard' },
    ],
  },
  {
    path: 'login',
    component: LoginPage,
    title: 'Login',
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordPage,
    title: 'Forgot Password',
  },
  {
    path: 'reset-password',
    component: ResetPasswordPage,
    title: 'Reset Password',
  },
  {
    path: 'setup-password',
    component: SetupPasswordPage,
    title: 'Setup Password',
  },
  {
    path: '**',
    component: NotFoundPage,
    title: '404 Not Found',
  },
];
