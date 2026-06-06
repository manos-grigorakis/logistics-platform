import { ReconiliationReportSummary } from './reconsiliation-report-summary';

export interface ReconciliationProcessResponse {
  totalInvoices: number;
  matchedInvoices: number;
  unmatchedInvoices: number;
  matchedTransactions: number;
  unmatchedTransaction: number;
  paidInvoices: number;
  partiallyPaidInvoice: number;
  outstandingInvoices: number;
  disputedInvoices: number;
  reportSummary: ReconiliationReportSummary;
}
