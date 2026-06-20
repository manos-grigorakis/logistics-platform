import { Pagination } from '../../../shared/models/pagination';
import { SupplierPayment } from './supplier-payments.interface';

export interface SupplierPaymentsList extends Pagination {
  content: SupplierPayment[];
}
