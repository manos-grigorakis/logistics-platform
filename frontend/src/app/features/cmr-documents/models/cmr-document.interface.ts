export interface CmrDocument {
  id: number;
  shipmentId: number;
  number: string;
  status: string;
  fileUrl?: string;
  senderSigned: boolean;
  carrierSigned: boolean;
  consigneeSigned: boolean;
  createdAt: string;
  updatedAt: string;
}
