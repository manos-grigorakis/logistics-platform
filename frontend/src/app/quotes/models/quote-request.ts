import { QuoteItems } from './quote-items';

export interface QuoteRequest {
  customerId: number;
  userId: number;
  origin: string;
  destination: string;
  validityDays: number;
  notes: string | null;
  specialTerms: string | null;
  quoteItems: QuoteItems[];
}
