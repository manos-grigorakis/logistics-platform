import { CustomerSummary } from './customer-summary';
import { QuoteItems } from './quote-items';

export interface Quote {
  id?: number;
  customer: CustomerSummary;
  origin: string;
  destination: string;
  validityDays: number;
  quoteStatus?: string;
  quoteItems: QuoteItems[];
  notes: string | null;
  specialTerms: string | null;
}
