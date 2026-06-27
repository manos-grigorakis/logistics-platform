import { Supplier } from './supplier.interface';
import { Page } from '../../../shared/models/page.interface';

export interface SupplierListResponse {
  content: Supplier[];
  page: Page;
}
