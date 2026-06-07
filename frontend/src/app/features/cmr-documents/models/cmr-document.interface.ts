export interface CmrDocument {
  id: number;
  shipmentId: number;
  number: string;
  status: string;
  fileUrl?: string;
  signedAt: string | null;
  signedBy: string | null;
  createdAt: string;
  updatedAt: string;
}
