export function quoteStatusBadgeColor(status: string): string {
  switch (status) {
    case 'draft':
      return 'bg-secondary-100 text-secondary-800';
    case 'sent':
      return 'bg-primary-100 text-primary-800';
    case 'accepted':
      return 'bg-success-light text-success-dark';
    case 'expired':
      return 'bg-warning-light text-warning-dark';
    case 'rejected':
      return 'bg-danger-light text-danger-dark';
    case 'cancelled':
      return 'bg-secondary-200 text-secondary-700';
    case 'converted':
      return 'bg-info-light text-info-dark';
    default:
      return 'bg-slate-200 text-slate-700';
  }
}
