import { Pagination } from '../../../shared/models/pagination';
import { Supplier } from './supplier.interface';

export interface SupplierListResponse extends Pagination {
  content: Supplier[];
}
