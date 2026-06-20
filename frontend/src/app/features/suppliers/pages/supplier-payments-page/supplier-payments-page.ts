import { Component, inject, OnInit } from '@angular/core';
import { SupplierPaymentsTable } from '../../components/supplier-payments-table/supplier-payments-table';
import { SupplierPaymentsService } from '../../services/supplier-payments.service';
import { FetchSupplierPaymentsParams } from '../../models/fetch-supplier-payments-params.interface';
import { LanguageService } from '../../../../core/services/language.service';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { SupplierPayment } from '../../models/supplier-payments.interface';
import { finalize } from 'rxjs';
import { Pagination } from '../../../../shared/ui/pagination/pagination';

@Component({
  selector: 'app-supplier-payments-page',
  imports: [SupplierPaymentsTable, Pagination],
  templateUrl: './supplier-payments-page.html',
  styleUrl: './supplier-payments-page.css',
})
export class SupplierPaymentsPage implements OnInit {
  public payments: SupplierPayment[] = [];

  // UI
  public isLoading: boolean = false;

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  public pageSize: number = 0;

  // Services
  private paymentsService = inject(SupplierPaymentsService);
  private languageService = inject(LanguageService);

  private currentParams: FetchSupplierPaymentsParams = { page: 0 };

  ngOnInit(): void {
    this.fetchSupplierPayments({ page: 0 });
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.fetchSupplierPayments({ page: page });
  }

  private fetchSupplierPayments(params?: FetchSupplierPaymentsParams): void {
    this.isLoading = true;

    // Merge current state params with new params
    const finalParams: FetchSupplierPaymentsParams = {
      ...this.currentParams,
      ...params,
    };

    // Saved for future requests
    this.currentParams = finalParams;

    this.paymentsService
      .fetchSupplierPayments()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          const data = res.data;
          this.payments = data.content;

          // Pagination
          this.currentPage = data.number;
          this.totalPages = data.totalPages;
          this.totalElements = data.totalElements;
          this.pageSize = data.size;
        },
        error: (err) => this.languageService.toastError(handleHttpErrors(err.status)),
      });
  }
}
