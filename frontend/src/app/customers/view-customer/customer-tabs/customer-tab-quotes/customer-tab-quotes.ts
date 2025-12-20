import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CustomersService } from '../../../customers.service';
import { debounceTime, distinctUntilChanged, filter, map, Subject, Subscription } from 'rxjs';
import { NgClass, CurrencyPipe, DatePipe, TitleCasePipe } from '@angular/common';
import { ModalFile } from '../../../../shared/ui/modal-file/modal-file';
import { QuotesService } from '../../../../quotes/quotes.service';
import { quoteStatusBadgeColor } from '../../../../quotes/utils/quotes-status-badge-color';
import { QuotePerCustomer } from '../../../models/quotes-per-customer';
import { Pagination } from '../../../../shared/ui/pagination/pagination';
import { MetadataService } from '../../../../metadata/metadata.service';
import { FormsModule } from '@angular/forms';
import { toast } from 'ngx-sonner';
import { Modal } from '../../../../shared/ui/modal/modal';
import { QuotesFilters } from '../../../../quotes/quotes-filters/quotes-filters';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { QuotesPerCustomerParameters } from '../../../models/quotes-per-customer-parameters';

@Component({
  selector: 'app-customer-tab-quotes',
  imports: [
    NgClass,
    CurrencyPipe,
    DatePipe,
    TitleCasePipe,
    ModalFile,
    Pagination,
    FormsModule,
    Modal,
    QuotesFilters,
    LoadingSpinner,
  ],
  templateUrl: './customer-tab-quotes.html',
  styleUrl: './customer-tab-quotes.css',
})
export class CustomerTabQuotes implements OnInit, OnDestroy {
  public isQuotesLoading: boolean = false;
  public isQuotePdfLoading: boolean = false;
  public quotes: QuotePerCustomer[] = [];
  public errorMessage?: string = undefined;
  public quotesErrorMessage?: string = undefined;
  public quotesStatuses: string[] = [];
  public finalizedStatuses: string[] = ['accepted', 'rejected', 'cancelled', 'expired'];

  // PDF Modal
  public isModalOpen: boolean = false;
  public pdfUrl?: string = undefined;

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  private quotesToShow: number = 5;
  public pageSize: number = this.quotesToShow;

  // Quotes Status Modal
  public isQuotesStatusModalEnabled: boolean = false;
  public quoteStatusModalHeader?: string = undefined;
  public quoteStatusModalMessage?: string = undefined;

  // Filters
  public activeSortLabel: string = 'Sort by';
  public activeFilterLabel: string = 'Filter by';
  private searchChanged$ = new Subject<string>();
  private subSearch$?: Subscription;
  private currentParams: QuotesPerCustomerParameters = {
    page: 0,
  };

  private customersService = inject(CustomersService);
  private quotesService = inject(QuotesService);
  private metadataService = inject(MetadataService);

  private sub$?: Subscription;
  private quotesStatuses$?: Subscription;
  private quotesReqSub$?: Subscription;

  public editingQuoteId?: number;
  public pendingStatus?: string;
  private quoteToBeUpdated?: QuotePerCustomer = undefined;
  private desiredQuoteStatus?: string = undefined;

  ngOnInit(): void {
    this.fetchQuotesPerCustomer({ size: this.quotesToShow });
    this.metadataService.fetchQuotesStatuses();
    this.subscribeToQuotesStatuses();

    // Debouncer on search
    this.subSearch$ = this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((value) => this.onSearch(value));
  }

  ngOnDestroy(): void {
    this.sub$?.unsubscribe();
    this.quotesStatuses$?.unsubscribe();
    this.subSearch$?.unsubscribe();
    this.quotesReqSub$?.unsubscribe();
  }

  public onSearchChanged(value: string): void {
    this.searchChanged$.next(value);
  }

  public openQuote(id: number): void {
    this.fetchQuote(id);
    this.isModalOpen = true;
  }

  public closeQuote(): void {
    this.isModalOpen = false;
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.fetchQuotesPerCustomer({ page: page, size: this.quotesToShow });
  }

  public quoteStatusBadgeColor(status: string): string {
    return quoteStatusBadgeColor(status);
  }

  public onQuotesStatusModalConfirmClick(): void {
    if (!this.quoteToBeUpdated || !this.desiredQuoteStatus) return;

    // Close modal
    this.isQuotesStatusModalEnabled = false;

    // Update
    this.onStatusChange(this.quoteToBeUpdated, this.desiredQuoteStatus);

    // Reset
    this.resetQuoteStatusModalVariables();
  }

  public onQuotesStatusModalCancelClick(): void {
    this.isQuotesStatusModalEnabled = false;

    // Reset
    this.resetQuoteStatusModalVariables();
  }

  public handleQuotesStatusModal(quote: QuotePerCustomer, newStatus: string): void {
    this.quoteToBeUpdated = quote;
    this.desiredQuoteStatus = newStatus;
    this.editingQuoteId = quote.id;
    this.pendingStatus = newStatus;

    // Setup and enable modal
    this.quoteStatusModalHeader = `Update quote status`;
    this.quoteStatusModalMessage =
      `You are about to update the status of quote ${quote.number}. ` +
      `This action cannot be undone. ` +
      `Current: ${quote.status.toUpperCase()} → New: ${newStatus.toUpperCase()}`;
    this.isQuotesStatusModalEnabled = true;
  }

  public onStatusChange(quote: QuotePerCustomer, newStatus?: string): void {
    if (!newStatus) return;
    const normalized = newStatus.toLowerCase();

    if (!normalized || quote.status === normalized) return;
    if (!quote.id) return;

    const oldStatus = quote.status;
    quote.status = normalized;

    this.quotesService.updateQuoteStatus(quote.id, normalized).subscribe({
      next: () => {
        this.editingQuoteId = undefined;
        this.pendingStatus = undefined;
        toast.success('Quote status updated successfully');
      },
      error: (err) => {
        // Reset to previous value
        quote.status = oldStatus;
        this.editingQuoteId = undefined;
        this.pendingStatus = undefined;

        let errorMessage = err.error;
        let currentStatus = errorMessage?.details?.currentStatus;
        let desiredStatus = errorMessage?.details?.desiredStatus;

        if (err.status === 409 && currentStatus && desiredStatus) {
          toast.error(
            `You can't update from ${currentStatus.toUpperCase()} to ${desiredStatus.toUpperCase()}`,
          );
          return;
        }

        if (err.status === 404) {
          toast.error('Quote not found');
        } else if (err.status === 500) {
          toast.error('Server error. Please try again');
        } else {
          toast.error('An error occurred. Please try again');
        }
      },
    });
  }

  public onRefresh(): void {
    this.fetchQuotesPerCustomer({ size: this.quotesToShow });
  }

  public onSort(query: string) {
    if (!query) return;

    switch (query) {
      case 'sort-all':
        this.activeSortLabel = 'Sort by';
        this.fetchQuotesPerCustomer({ page: 0, sortBy: undefined, sortDirection: undefined });
        break;
      case 'sort-asc-by-number':
        this.activeSortLabel = 'Number 0 → 9';
        this.fetchQuotesPerCustomer({ page: 0, sortBy: 'number', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-number':
        this.activeSortLabel = 'Number 9 → 0';
        this.fetchQuotesPerCustomer({ page: 0, sortBy: 'number', sortDirection: 'desc' });
        break;
      case 'sort-asc-by-issue-date':
        this.activeSortLabel = 'Date 0 → 9';
        this.fetchQuotesPerCustomer({ page: 0, sortBy: 'issueDate', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-issue-date':
        this.activeSortLabel = 'Date 9 → 0';
        this.fetchQuotesPerCustomer({ page: 0, sortBy: 'issueDate', sortDirection: 'desc' });
        break;
    }
  }

  public onFilter(query: string) {
    if (!query) return;

    switch (query) {
      case 'filter-by-all':
        this.activeFilterLabel = 'Filter by';
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: undefined, sortDirection: undefined });
        break;
      case 'filter-by-quote-status-draft':
        this.activeFilterLabel = 'Draft';
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'DRAFT' });
        break;
      case 'filter-by-quote-status-sent':
        this.activeFilterLabel = 'Sent';
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'SENT' });
        break;
      case 'filter-by-quote-status-accepted':
        this.activeFilterLabel = 'Accepted';
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'ACCEPTED' });
        break;
      case 'filter-by-quote-status-rejected':
        this.activeFilterLabel = 'Rejected';
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'REJECTED' });
        break;
      case 'filter-by-quote-status-expired':
        this.activeFilterLabel = 'Expired';
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'EXPIRED' });
        break;
      case 'filter-by-quote-status-cancelled':
        this.activeFilterLabel = 'Cancelled';
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'CANCELLED' });
        break;
    }
  }

  private fetchQuotesPerCustomer(params?: QuotesPerCustomerParameters): void {
    this.sub$?.unsubscribe();

    const finalParams: QuotesPerCustomerParameters = {
      ...this.currentParams,
      ...params,
    };

    // Saved for future requests
    this.currentParams = finalParams;

    this.sub$ = this.customersService.selectedCustomer$.subscribe((c) => {
      if (!c) return;
      this.isQuotesLoading = true;
      this.errorMessage = undefined;
      this.quotesReqSub$?.unsubscribe();

      this.quotesReqSub$ = this.customersService.quotesPerCustomer(c.id, finalParams).subscribe({
        next: (res) => {
          this.isQuotesLoading = false;
          this.quotes = res.content;

          this.currentPage = res.number;
          this.totalPages = res.totalPages;
          this.totalElements = res.totalElements;
        },
        error: (err) => {
          this.isQuotesLoading = false;
          if (err.status === 500) {
            this.errorMessage = 'Server error. Please try again';
          } else {
            this.errorMessage = 'An error occured. Please try again';
          }
        },
      });
    });
  }

  private fetchQuote(id: number): void {
    this.isQuotePdfLoading = true;
    this.quotesErrorMessage = undefined;

    this.quotesService.fetchQuoteById(id).subscribe({
      next: (res) => {
        this.isQuotePdfLoading = false;
        this.pdfUrl = res.pdfUrl;
      },
      error: (err) => {
        this.isQuotePdfLoading = false;
        if (err.status === 404) {
          this.quotesErrorMessage = `Quote with id ${id} not exist`;
        } else if (err.status === 500) {
          this.quotesErrorMessage = 'Server error. Please try again';
        } else {
          this.quotesErrorMessage = 'An error occured. Please try again later';
        }
      },
    });
  }

  private subscribeToQuotesStatuses(): void {
    this.quotesStatuses$ = this.metadataService.quoteStatuses$
      .pipe(
        filter((statuses) => statuses.length > 0),
        map((statuses) => statuses.filter((s) => s !== 'EXPIRED')),
      )
      .subscribe((statuses) => {
        this.quotesStatuses = statuses;
      });
  }

  private resetQuoteStatusModalVariables(): void {
    this.quoteToBeUpdated = undefined;
    this.desiredQuoteStatus = undefined;
    this.editingQuoteId = undefined;
    this.pendingStatus = undefined;
  }

  private onSearch(value: string): void {
    let param = value.trim();

    if (param.length === 0) {
      this.fetchQuotesPerCustomer({ page: 0, size: this.quotesToShow, number: undefined });
      return;
    }

    this.activeFilterLabel = 'Filter by';
    this.activeSortLabel = 'Sort by';
    this.fetchQuotesPerCustomer({ size: this.quotesToShow, number: param });
  }
}
