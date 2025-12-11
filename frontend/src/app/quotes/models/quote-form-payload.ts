import { QuoteItems } from './quote-items';

export interface QuoteFormPayload {
  customerId: number;
  origin: string;
  destination: string;
  validityDays: number;
  notes: string | null;
  specialTerms: string | null;
  items: QuoteItems[];
}
