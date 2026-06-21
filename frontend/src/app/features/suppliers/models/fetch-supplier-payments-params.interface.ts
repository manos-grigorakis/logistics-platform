import { FilterParams } from '../../../shared/models/filter-params';

export interface FetchSupplierPaymentsParams extends FilterParams {
  number?: string;
}
