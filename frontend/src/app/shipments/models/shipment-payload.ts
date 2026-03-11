import { CargoItems } from './cargo-items';

export interface ShipmentPayload {
  quoteId: number;
  driverId: number | null;
  createdByUserId: number;
  truckId: number | null;
  trailerId: number | null;
  pickup: string;
  notes: string | null;
  cargoItems?: CargoItems[];
}
