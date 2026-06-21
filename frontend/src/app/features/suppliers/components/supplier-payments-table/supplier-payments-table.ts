import { Component, Input } from '@angular/core';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { SupplierPayment } from '../../models/supplier-payments.interface';
import { CurrencyPipe, DatePipe, NgClass } from '@angular/common';
import { RouterLink } from '@angular/router';
import { supplierPaymentStatusBadgeColor } from '../../utils/supplier-payment-status-badge-color.util';

@Component({
  selector: 'app-supplier-payments-table',
  imports: [LoadingSpinner, TranslatePipe, CurrencyPipe, DatePipe, RouterLink, NgClass],
  templateUrl: './supplier-payments-table.html',
  styleUrl: './supplier-payments-table.css',
})
export class SupplierPaymentsTable {
  @Input() isLoading?: boolean;
  @Input({ required: true }) supplierPayment?: SupplierPayment[];

  public applyStatusBadgeColor(status: string): string {
    return supplierPaymentStatusBadgeColor(status.toUpperCase());
  }
}
