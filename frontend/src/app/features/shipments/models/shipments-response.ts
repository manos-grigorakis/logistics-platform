import { Shipment } from './shipment';
import { Page } from '../../../shared/models/page.interface';

export interface ShipmentsResponse {
  content: Shipment[];
  page: Page;
}
