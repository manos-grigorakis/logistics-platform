import { QuotePerCustomer } from './quotes-per-customer';

export interface QuotePerCustomerResponse {
  content: QuotePerCustomer[];
  last: boolean;
  totalPages: number;
  totalElements: number;
  first: boolean;
  size: number;
  number: number;
  numberOfElements: number;
  empty: boolean;
}
