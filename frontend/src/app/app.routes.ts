import { Routes } from '@angular/router';
import { LoginForm } from './auth/login-form/login-form';
import { authGuard } from './guards/auth-guard';
import { NotFoundPage } from './not-found-page/not-found-page';
import { ForgotPasswordForm } from './auth/forgot-password-form/forgot-password-form';
import { ResetPasswordForm } from './auth/reset-password-form/reset-password-form';
import { SetupPasswordForm } from './auth/setup-password-form/setup-password-form';
import { MainLayout } from './layout/main-layout/main-layout';
import { MainDashboard } from './dashboard/main-dashboard/main-dashboard';
import { UsersPage } from './users/users-page/users-page';
import { CreateUserPage } from './users/create-user-page/create-user-page';
import { EditUserPage } from './users/edit-user-page/edit-user-page';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    title: 'Logistics Platform',
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: MainDashboard, title: 'Dashboard' },
      {
        path: 'users',
        children: [
          { path: '', component: UsersPage, title: 'Users' },
          { path: 'create-user', component: CreateUserPage, title: 'Create User' },
          { path: 'edit-user/:id', component: EditUserPage, title: 'Edit User' },
        ],
      },
    ],
  },
  {
    path: 'login',
    component: LoginForm,
    title: 'Login',
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordForm,
    title: 'Forgot Password',
  },
  {
    path: 'reset-password',
    component: ResetPasswordForm,
    title: 'Reset Password',
  },
  {
    path: 'setup-password',
    component: SetupPasswordForm,
    title: 'Setup Password',
  },
  {
    path: '**',
    component: NotFoundPage,
    title: '404 Not Found',
  },
];
