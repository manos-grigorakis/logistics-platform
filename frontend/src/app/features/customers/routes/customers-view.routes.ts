import { CustomerTabQuotes } from '../pages/view-customer/customer-tabs/customers-tab-quotes/customer-tab-quotes';
import { CustomerTabShipments } from '../pages/view-customer/customer-tabs/customers-tab-shipments/customer-tab-shipments';
import { Routes } from '@angular/router';

export default [
  { path: '', pathMatch: 'full', redirectTo: 'tab-quotes' },
  { path: 'tab-quotes', component: CustomerTabQuotes },
  { path: 'tab-shipments', component: CustomerTabShipments },
] satisfies Routes;
