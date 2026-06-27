import { QuotePerCustomer } from './quotes-per-customer';
import { Page } from '../../../shared/models/page.interface';

export interface QuotePerCustomerResponse {
  content: QuotePerCustomer[];
  page: Page;
}
