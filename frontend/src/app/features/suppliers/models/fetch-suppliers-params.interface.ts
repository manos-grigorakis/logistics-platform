import { FilterParams } from '../../../shared/models/filter-params';

export interface FetchSuppliersParams extends FilterParams {
  companyName?: string;
}
