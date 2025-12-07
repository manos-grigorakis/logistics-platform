import { QuotesListItem } from './quotes-list-item';

export interface FetchQuotesResponse {
  content: QuotesListItem[];
  last: boolean;
  totalPages: number;
  totalElements: number;
  first: boolean;
  size: number;
  number: number;
  numberOfElements: number;
  empty: boolean;
}
