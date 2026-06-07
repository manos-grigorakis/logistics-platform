import { FilterParams } from '../../../shared/models/filter-params';

export interface CmrDocumentFilterRequest extends FilterParams {
  number?: string;
  status?: string;
}
