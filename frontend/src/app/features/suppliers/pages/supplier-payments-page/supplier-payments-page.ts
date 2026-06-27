import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { SupplierPaymentsTable } from '../../components/supplier-payments-table/supplier-payments-table';
import { SupplierPaymentsService } from '../../services/supplier-payments.service';
import { FetchSupplierPaymentsParams } from '../../models/fetch-supplier-payments-params.interface';
import { LanguageService } from '../../../../core/services/language.service';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { SupplierPayment } from '../../models/supplier-payments.interface';
import { debounceTime, distinctUntilChanged, finalize, Subject, Subscription, take } from 'rxjs';
import { Pagination } from '../../../../shared/ui/pagination/pagination';
import { SupplierPaymentsFilters } from '../../components/supplier-payments-filters/supplier-payments-filters';
import { SortOption } from '../../../../shared/models/sort-option.interface';
import { Page } from '../../../../shared/models/page.interface';

@Component({
  selector: 'app-supplier-payments-page',
  imports: [SupplierPaymentsTable, Pagination, SupplierPaymentsFilters],
  templateUrl: './supplier-payments-page.html',
  styleUrl: './supplier-payments-page.css',
})
export class SupplierPaymentsPage implements OnInit, OnDestroy {
  public payments: SupplierPayment[] = [];

  // UI
  public isLoading: boolean = false;

  // Pagination
  public page: Page = { size: 0, number: 0, totalElements: 0, totalPages: 0 };

  // Filterint - Sorting
  public activeSortLabel: string = '';
  private searchChanged$ = new Subject<string>();
  private currentParams: FetchSupplierPaymentsParams = { page: 0 };

  // Services
  private paymentsService = inject(SupplierPaymentsService);
  private languageService = inject(LanguageService);

  // Subscriptions
  private langChangeSub?: Subscription;

  // Lifecycle
  ngOnInit(): void {
    this.fetchSupplierPayments();

    // Debouncer on search
    this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((val) => this.onSearch(val));

    this.setLabels();
    this.langChangeSub = this.languageService.onLangChange.subscribe(() => this.setLabels());
  }

  ngOnDestroy(): void {
    this.langChangeSub?.unsubscribe();
  }

  public onSearchChanged(value: string): void {
    this.searchChanged$.next(value);
  }

  public onSearch(keyword: string): void {
    const value = keyword.trim();

    if (value.length === 0) {
      this.fetchSupplierPayments({ page: 0, number: undefined });
      return;
    }

    if (value.startsWith('SP-')) {
      this.fetchSupplierPayments({ page: 0, number: value });
    }
  }

  public onRefresh(): void {
    this.fetchSupplierPayments({ page: 0 });
  }

  public onSortByField(option: SortOption | undefined): void {
    this.fetchSupplierPayments({ sortBy: option?.sortBy, sortDirection: option?.sortDirection });

    if (option === undefined) {
      this.activeSortLabel = this.languageService.translateKey('common.filters.sort-by');
    } else {
      let directionLabel: string = '';
      if (option?.sortDirection === 'asc') directionLabel = '(0-9)';
      else directionLabel = '(9-0)';

      this.activeSortLabel = `${this.languageService.translateKey(`common.fields.${option.label}`)} ${directionLabel}`;
    }
  }

  public onPageChange(page: number): void {
    if (page === this.page.number) return;

    this.page.number = page;
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
      .fetchSupplierPayments(finalParams)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          const data = res.data;
          this.payments = data.content;
          this.page = data.page;
        },
        error: (err) => this.languageService.toastError(handleHttpErrors(err.status)),
      });
  }

  private setLabels(): void {
    this.languageService
      .translateKeyAsync('common.filters.sort-by')
      .pipe(take(1))
      .subscribe((val) => (this.activeSortLabel = val));
  }
}
