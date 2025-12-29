export interface Shipment {
  id: number;
  number: string;
  status: string;
  pickup: string;
  notes: string;
  createdAt: string;
  updatedAt?: string;
  quote: {
    id: number;
    number: string;
  };
  driver?: {
    id: number;
    fullName: string;
  };
  createdByUser: {
    id: number;
    fullName: string;
  };
  truck?: {
    id: number;
    plate: string;
    type: string;
  };
  trailer?: {
    id: number;
    plate: string;
    type: string;
  };
}
