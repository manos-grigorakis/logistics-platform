import { Routes } from '@angular/router';
import { LoginPage } from './auth/login-page/login-page';
import { DashboardPage } from './dashboard/dashboard-page/dashboard-page';
import { authGuard } from './guards/auth-guard';
import { NotFoundPage } from './not-found-page/not-found-page';
import { ForgotPasswordPage } from './auth/forgot-password-page/forgot-password-page';

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
    path: 'reset-password',
    component: ForgotPasswordPage,
    title: 'Reset Password',
  },
  {
    path: '**',
    component: NotFoundPage,
    title: '404 Not Found',
  },
];
