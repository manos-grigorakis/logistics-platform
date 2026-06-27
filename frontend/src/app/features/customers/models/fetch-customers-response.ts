import { Customer } from './customer';
import { Page } from '../../../shared/models/page.interface';

export interface FetchCustomersResponse {
  content: Customer[];
  page: Page;
}
