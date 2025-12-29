import { FilterParams } from '../../shared/models/filter-params';
import { ShipmentStatus } from './shipment-status';

export interface ShipmentParams extends FilterParams {
  number?: string;
  status?: string;
  pickupFrom?: string;
  pickupTo?: string;
  driverId?: number;
}
