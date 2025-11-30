import { CustomerType } from './customer-type';

export interface FetchCustomersParameters {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
  tin?: string;
  companyName?: string;
  customerType?: CustomerType;
}
