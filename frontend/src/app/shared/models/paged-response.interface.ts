import { Page } from './page.interface';

export interface PagedResponse<T> {
  content: T[];
  page: Page;
}
