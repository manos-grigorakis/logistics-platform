export function quoteStatusBadgeColor(status: string): string {
  switch (status) {
    case 'DRAFT':
      return 'bg-secondary-100 text-secondary-800';
    case 'SENT':
      return 'bg-primary-100 text-primary-800';
    case 'ACCEPTED':
      return 'bg-success-light text-success-dark';
    case 'EXPIRED':
      return 'bg-warning-light text-warning-dark';
    default:
      return 'bg-slate-200 text-slate-700';
  }
}
