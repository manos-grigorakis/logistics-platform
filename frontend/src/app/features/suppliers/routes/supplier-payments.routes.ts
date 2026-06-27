import { SupplierPaymentsPage } from '../pages/supplier-payments-page/supplier-payments-page';
import { SupplierPaymentsCreate } from '../pages/supplier-payments-create/supplier-payments-create';
import { EditSupplierPayment } from '../pages/edit-supplier-payment/edit-supplier-payment';
import { SupplierPaymentView } from '../pages/supplier-payment-view/supplier-payment-view';
import { Routes } from '@angular/router';

export default [
  { path: '', component: SupplierPaymentsPage, title: 'Suppliers Payments' },
  { path: 'create', component: SupplierPaymentsCreate, title: 'Create Supplier Payment' },
  { path: 'edit/:id', component: EditSupplierPayment, title: 'Edit Supplier Payment' },
  { path: ':id', component: SupplierPaymentView, title: 'Suppliers Payment' },
] satisfies Routes;
