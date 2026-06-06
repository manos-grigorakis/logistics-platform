export interface CmrDocumentSummaryResponse {
  id: number;
  number: string;
  status: string;
  fileUrl: string;
  signedBy: string;
  signedAt: Date;
  createdAt: Date;
  updatedAt: Date;
}
