import { Routes } from '@angular/router';
import { LoginPage } from './auth/login-page/login-page';
import { DashboardPage } from './dashboard/dashboard-page/dashboard-page';
import { authGuard } from './guards/auth-guard';
import { NotFoundPage } from './not-found-page/not-found-page';
import { ForgotPasswordPage } from './auth/forgot-password-page/forgot-password-page';
import { ResetPasswordPage } from './auth/reset-password-page/reset-password-page';
import { SetupPasswordPage } from './auth/setup-password-page/setup-password-page';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: LoginPage,
    title: 'Login',
  },
  {
    path: 'dashboard',
    component: DashboardPage,
    title: 'Dashboard',
    canActivate: [authGuard],
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
