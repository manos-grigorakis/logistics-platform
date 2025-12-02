import { Customer } from './customer';

export interface FetchCustomersResponse {
  content: Customer[];
  last: boolean;
  totalPages: number;
  totalElements: number;
  first: boolean;
  size: number;
  number: number;
  numberOfElements: number;
  empty: boolean;
}
