import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MetadataService } from '../../../../core/metadata/metadata.service';
import { SupplierPaymentsService } from '../../services/supplier-payments.service';
import { LanguageService } from '../../../../core/services/language.service';
import { BehaviorSubject, finalize } from 'rxjs';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { SupplierPayment } from '../../models/supplier-payments.interface';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { CurrencyPipe, DatePipe, NgClass } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { NgIcon } from '@ng-icons/core';
import { ModalFile } from '../../../../shared/ui/modal-file/modal-file';
import { StatBox } from '../../../../shared/ui/stat-box/stat-box';
import { supplierPaymentStatusBadgeColor } from '../../utils/supplier-payment-status-badge-color.util';
import { InfoBanner } from '../../../../shared/ui/info-banner/info-banner';
import { SectionHeader } from '../../../../shared/ui/section-header/section-header';

@Component({
  selector: 'app-supplier-payment-view',
  imports: [
    LoadingSpinner,
    DatePipe,
    TranslatePipe,
    DatePipe,
    CurrencyPipe,
    NgClass,
    NgIcon,
    ModalFile,
    StatBox,
    InfoBanner,
    SectionHeader,
  ],
  templateUrl: './supplier-payment-view.html',
  styleUrl: './supplier-payment-view.css',
})
export class SupplierPaymentView implements OnInit {
  public isLoading: boolean = false;
  public payment: SupplierPayment | null = null;
  public statuses$?: BehaviorSubject<string[]>;

  // Modals
  public showInvoiceModal: boolean = false;
  public showReceiptModal: boolean = false;

  private id: number | null = null;
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  // Services
  private paymentsService = inject(SupplierPaymentsService);
  private metadataService = inject(MetadataService);
  private languageService = inject(LanguageService);

  ngOnInit() {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) {
      this.router.navigate(['/suppliers/payments']);
      return;
    }

    this.id = parseInt(tempId);
    this.fetchSupplierPayment(this.id);

    this.metadataService.fetchSupplierPaymentsStatuses();
    this.statuses$ = this.metadataService.supplierPaymentsStatuses$;
  }

  public applyFileIcon(fileUrl: string | undefined): string {
    if (!fileUrl) return 'lucideFileXCorner';
    else return 'LucideFileText';
  }

  public applyStatusBadgeColor(status: string): string {
    return supplierPaymentStatusBadgeColor(status.toUpperCase());
  }

  private fetchSupplierPayment(id: number): void {
    this.isLoading = true;

    this.paymentsService
      .fetchSupplierPaymentById(id)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          this.payment = res.data;
          console.log(this.payment);
        },
        error: (err) => {
          const errorStatus = err.status;

          if (err.status === 404) {
            this.languageService.toastError('suppliers.messages.payment-supplier-not-found');
            this.router.navigate(['/suppliers/payments']);
          } else {
            this.languageService.toastError(handleHttpErrors(errorStatus));
          }
        },
      });
  }
}
