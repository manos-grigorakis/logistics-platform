import { CustomerSummary } from './customer-summary';
import { QuoteItems } from './quote-items';

export interface QuoteResponse {
  id: number;
  number: string;
  pdfUrl: string;
  issueDate: string;
  validityDays: number;
  expirationDate: string;
  origin: string;
  destination: string;
  taxRatePercentage: number;
  netPrice: number;
  vatAmount: number;
  grossPrice: number;
  notes: string;
  specialTerms: string;
  quoteStatus: string;
  createdAt: string;
  updatedAt: string | null;
  userId: number;
  customer: CustomerSummary;
  quoteItems: QuoteItems[];
}
