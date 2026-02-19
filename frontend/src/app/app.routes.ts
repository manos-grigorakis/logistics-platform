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
import { RolesPage } from './roles/roles-page/roles-page';
import { CreateRole } from './roles/create-role/create-role';
import { EditRole } from './roles/edit-role/edit-role';
import { roleGuard } from './guards/role-guard';
import { CustomersPage } from './customers/customers-page/customers-page';
import { CreateCustomer } from './customers/create-customer/create-customer';
import { EditCustomer } from './customers/edit-customer/edit-customer';
import { ViewCustomer } from './customers/view-customer/view-customer';
import { QuotesPage } from './quotes/quotes-page/quotes-page';
import { CreateQuote } from './quotes/create-quote/create-quote';
import { EditQuote } from './quotes/edit-quote/edit-quote';
import { ForbiddenPage } from './forbidden-page/forbidden-page';
import { CustomerTabQuotes } from './customers/view-customer/customer-tabs/customer-tab-quotes/customer-tab-quotes';
import { VehiclesPage } from './vehicles/vehicles-page/vehicles-page';
import { CreateVehicle } from './vehicles/create-vehicle/create-vehicle';
import { EditVehicle } from './vehicles/edit-vehicle/edit-vehicle';
import { ShipmentsPage } from './shipments/shipments-page/shipments-page';
import { CreateShipment } from './shipments/create-shipment/create-shipment';
import { EditShipment } from './shipments/edit-shipment/edit-shipment';

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
        canActivate: [roleGuard],
        children: [
          { path: '', component: UsersPage, title: 'Users' },
          { path: 'create-user', component: CreateUserPage, title: 'Create User' },
          { path: 'edit-user/:id', component: EditUserPage, title: 'Edit User' },
        ],
      },
      {
        path: 'roles',
        canActivate: [roleGuard],
        children: [
          { path: '', component: RolesPage, title: 'Roles' },
          { path: 'create-role', component: CreateRole, title: 'Create Role' },
          { path: 'edit-role/:id', component: EditRole, title: 'Edit Role' },
        ],
      },
      {
        path: 'customers',
        children: [
          { path: '', component: CustomersPage, title: 'Customers' },
          { path: 'create-customer', component: CreateCustomer, title: 'Create Customer' },
          { path: 'edit-customer/:id', component: EditCustomer, title: 'Edit Customer' },
          {
            path: 'view-customer/:id',
            component: ViewCustomer,
            title: 'View Customer',
            children: [{ path: 'tab-quotes', component: CustomerTabQuotes }],
          },
        ],
      },
      {
        path: 'quotes',
        children: [
          { path: '', component: QuotesPage, title: 'Quotes' },
          { path: 'create-quote', component: CreateQuote, title: 'Create Quote' },
          { path: 'edit-quote/:id', component: EditQuote, title: 'Quotes' },
        ],
      },
      {
        path: 'vehicles',
        children: [
          { path: '', component: VehiclesPage, title: 'Vehicles' },
          { path: 'create-vehicle', component: CreateVehicle, title: 'Create Vehicle' },
          { path: 'edit-vehicle/:id', component: EditVehicle, title: 'Edit Vehicle' },
        ],
      },
      {
        path: 'shipments',
        children: [
          { path: '', component: ShipmentsPage, title: 'Shipments' },
          { path: 'create-shipment', component: CreateShipment, title: 'Create Shipment' },
          { path: 'edit-shipment/:id', component: EditShipment, title: 'Edit Shipment' },
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
  { path: 'forbidden', component: ForbiddenPage, title: '403 Forbidden' },
  {
    path: '**',
    component: NotFoundPage,
    title: '404 Not Found',
  },
];
