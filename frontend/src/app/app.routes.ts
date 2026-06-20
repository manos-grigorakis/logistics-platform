import { Routes } from '@angular/router';
import { LoginForm } from './core/auth/login-form/login-form';
import { authGuard } from './core/guards/auth-guard';
import { NotFound } from './pages/not-found/not-found';
import { ForgotPasswordForm } from './core/auth/forgot-password-form/forgot-password-form';
import { ResetPasswordForm } from './core/auth/reset-password-form/reset-password-form';
import { SetupPasswordForm } from './core/auth/setup-password-form/setup-password-form';
import { MainLayout } from './core/layout/main-layout/main-layout';
import { MainDashboard } from './features/dashboard/pages/main-dashboard/main-dashboard';
import { UsersPage } from './features/users/pages/users-page/users-page';
import { CreateUser } from './features/users/pages/create-user/create-user';
import { EditUser } from './features/users/pages/edit-user/edit-user';
import { RolesPage } from './features/roles/pages/roles-page/roles-page';
import { CreateRole } from './features/roles/pages/create-role/create-role';
import { EditRole } from './features/roles/pages/edit-role/edit-role';
import { roleGuard } from './core/guards/role-guard';
import { CustomersPage } from './features/customers/pages/customers-page/customers-page';
import { CreateCustomer } from './features/customers/pages/create-customer/create-customer';
import { EditCustomer } from './features/customers/pages/edit-customer/edit-customer';
import { ViewCustomer } from './features/customers/pages/view-customer/view-customer';
import { QuotesPage } from './features/quotes/pages/quotes-page/quotes-page';
import { CreateQuote } from './features/quotes/pages/create-quote/create-quote';
import { EditQuote } from './features/quotes/pages/edit-quote/edit-quote';
import { Forbidden } from './pages/forbidden/forbidden';
import { CustomerTabQuotes } from './features/customers/pages/view-customer/customer-tabs/customers-tab-quotes/customer-tab-quotes';
import { VehiclesPage } from './features/vehicles/pages/vehicles-page/vehicles-page';
import { CreateVehicle } from './features/vehicles/pages/create-vehicle/create-vehicle';
import { EditVehicle } from './features/vehicles/pages/edit-vehicle/edit-vehicle';
import { ShipmentsPage } from './features/shipments/pages/shipments-page/shipments-page';
import { CreateShipment } from './features/shipments/pages/create-shipment/create-shipment';
import { EditShipment } from './features/shipments/pages/edit-shipment/edit-shipment';
import { ViewShipment } from './features/shipments/pages/view-shipment/view-shipment';
import { CustomerTabShipments } from './features/customers/pages/view-customer/customer-tabs/customers-tab-shipments/customer-tab-shipments';
import { PaymentsPage } from './features/payments/payments-page/payments-page';
import { CmrDocumentsPage } from './features/cmr-documents/pages/cmr-documents-page/cmr-documents-page';
import { UploadSignedCmr } from './features/cmr-documents/pages/upload-signed-cmr/upload-signed-cmr';
import { SuppliersPage } from './features/suppliers/pages/suppliers-page/suppliers-page';
import { SupplierPaymentsPage } from './features/suppliers/pages/supplier-payments-page/supplier-payments-page';
import { EditSupplier } from './features/suppliers/pages/edit-supplier/edit-supplier';
import { SuppliersCreate } from './features/suppliers/pages/suppliers-create/suppliers-create';
import { SupplierPaymentsCreate } from './features/suppliers/pages/supplier-payments-create/supplier-payments-create';
import { EditSupplierPayment } from './features/suppliers/pages/edit-supplier-payment/edit-supplier-payment';

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
          { path: 'create-user', component: CreateUser, title: 'Create User' },
          { path: 'edit-user/:id', component: EditUser, title: 'Edit User' },
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
            children: [
              { path: '', pathMatch: 'full', redirectTo: 'tab-quotes' },
              { path: 'tab-quotes', component: CustomerTabQuotes },
              { path: 'tab-shipments', component: CustomerTabShipments },
            ],
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
          { path: 'view-shipment/:id', component: ViewShipment, title: 'View Shipment' },
          { path: 'create-shipment', component: CreateShipment, title: 'Create Shipment' },
          { path: 'edit-shipment/:id', component: EditShipment, title: 'Edit Shipment' },
        ],
      },
      {
        path: 'payments-tracking',
        component: PaymentsPage,
        title: 'Payments Tracking',
      },
      {
        path: 'cmr-documents',
        children: [
          { path: '', component: CmrDocumentsPage, title: 'CMR Documents' },
          {
            path: 'upload-signed',
            component: UploadSignedCmr,
            title: 'Upload Signed CMR Document',
          },
        ],
      },

      // Suppliers
      {
        path: 'suppliers',
        children: [
          { path: '', component: SuppliersPage, title: 'Suppliers' },
          { path: 'create', component: SuppliersCreate, title: 'Create Supplier' },
          { path: 'edit/:id', component: EditSupplier, title: 'Edit Supplier' },
          {
            path: 'payments',
            children: [
              { path: '', component: SupplierPaymentsPage, title: 'Suppliers Payments' },
              {
                path: 'create',
                component: SupplierPaymentsCreate,
                title: 'Create Supplier Payment',
              },
              { path: 'edit/:id', component: EditSupplierPayment, title: 'Edit Supplier Payment' },
            ],
          },
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
  { path: 'forbidden', component: Forbidden, title: '403 Forbidden' },
  {
    path: '**',
    component: NotFound,
    title: '404 Not Found',
  },
];
