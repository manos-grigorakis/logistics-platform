export interface CreatedQuoteResponse {
  id: number;
  number: string;
  issueDate: string;
  expirationDate: string;
  grossPrice: number;
  quoteStatus: string;
  pdfUrl: string;
  customerId: number;
  customerFullName: string;
}
