export function cmrDocumentStatusBadgeColor(status: string): string {
  switch (status) {
    case 'generated':
      return 'bg-success-light text-success-dark';
    case 'signed':
      return 'bg-primary-100 text-primary-800';
    case 'cancelled':
      return 'bg-warning-light text-warning-dark';
    default:
      return 'bg-slate-200 text-slate-700';
  }
}
