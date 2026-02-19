export interface ShipmentPayload {
  quoteId: number;
  driverId: number | null;
  createdByUserId: number;
  truckId: number | null;
  trailerId: number | null;
  pickup: Date;
  notes: string | null;
}
