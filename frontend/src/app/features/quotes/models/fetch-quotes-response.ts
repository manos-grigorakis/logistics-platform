import { QuotesListItem } from './quotes-list-item';
import { Page } from '../../../shared/models/page.interface';

export interface FetchQuotesResponse {
  content: QuotesListItem[];
  page: Page;
}
