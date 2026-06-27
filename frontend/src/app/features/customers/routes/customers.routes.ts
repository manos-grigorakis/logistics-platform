import { CustomersPage } from '../pages/customers-page/customers-page';
import { CreateCustomer } from '../pages/create-customer/create-customer';
import { EditCustomer } from '../pages/edit-customer/edit-customer';
import { ViewCustomer } from '../pages/view-customer/view-customer';
import { Routes } from '@angular/router';

export default [
  { path: '', component: CustomersPage, title: 'Customers' },
  { path: 'create-customer', component: CreateCustomer, title: 'Create Customer' },
  { path: 'edit-customer/:id', component: EditCustomer, title: 'Edit Customer' },
  {
    path: 'view-customer/:id',
    component: ViewCustomer,
    title: 'View Customer',
    loadChildren: () => import('./customers-view.routes'),
  },
] satisfies Routes;
