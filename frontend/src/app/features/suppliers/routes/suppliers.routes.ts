import { SuppliersPage } from '../pages/suppliers-page/suppliers-page';
import { SuppliersCreate } from '../pages/suppliers-create/suppliers-create';
import { EditSupplier } from '../pages/edit-supplier/edit-supplier';
import { Routes } from '@angular/router';

export default [
  { path: '', component: SuppliersPage, title: 'Suppliers' },
  { path: 'create', component: SuppliersCreate, title: 'Create Supplier' },
  { path: 'edit/:id', component: EditSupplier, title: 'Edit Supplier' },
  { path: 'payments', loadChildren: () => import('./supplier-payments.routes') },
] satisfies Routes;
