import { SupplierPayment } from './supplier-payments.interface';
import { Page } from '../../../shared/models/page.interface';

export interface SupplierPaymentsList {
  content: SupplierPayment[];
  page: Page;
}
