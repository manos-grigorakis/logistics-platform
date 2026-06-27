export interface FetchCustomersParameters {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
  tin?: string;
  companyName?: string;
  customerType?: 'COMPANY' | 'INDIVIDUAL';
}
