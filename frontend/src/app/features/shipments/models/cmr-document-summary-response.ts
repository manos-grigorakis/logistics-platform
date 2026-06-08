export interface CmrDocumentSummaryResponse {
  id: number;
  number: string;
  status: string;
  fileUrl: string;
  senderSigned: boolean;
  carrierSigned: boolean;
  consigneeSigned: boolean;
  createdAt: Date;
  updatedAt: Date;
}
