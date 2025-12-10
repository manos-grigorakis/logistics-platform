import { QuoteItems } from './quote-items';

export interface QuoteRequest {
  id?: number;
  customerId: number;
  userId: number;
  origin: string;
  destination: string;
  validityDays: number;
  notes?: string | null;
  specialTerms: string | null;
  quoteItems: QuoteItems[];
}
