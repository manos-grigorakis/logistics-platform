import { Pagination } from '../../shared/models/pagination';
import { Shipment } from './shipment';

export interface ShipmentsResponse extends Pagination {
  content: Shipment[];
}
