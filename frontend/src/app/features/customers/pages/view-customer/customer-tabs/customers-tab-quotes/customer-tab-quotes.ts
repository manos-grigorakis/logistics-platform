import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CustomersService } from '../../../../customers.service';
import { debounceTime, distinctUntilChanged, filter, map, Subject, Subscription, take } from 'rxjs';
import { NgClass } from '@angular/common';
import { ModalFile } from '../../../../../../shared/ui/modal-file/modal-file';
import { QuotesService } from '../../../../../quotes/quotes.service';
import { QuotePerCustomer } from '../../../../models/quotes-per-customer';
import { Pagination } from '../../../../../../shared/ui/pagination/pagination';
import { MetadataService } from '../../../../../../core/metadata/metadata.service';
import { FormsModule } from '@angular/forms';
import { toast } from 'ngx-sonner';
import { Modal } from '../../../../../../shared/ui/modal/modal';
import { QuotesFilters } from '../../../../../../shared/components/quotes-filters/quotes-filters';
import { LoadingSpinner } from '../../../../../../shared/ui/loading-spinner/loading-spinner';
import { QuotesPerCustomerParameters } from '../../../../models/quotes-per-customer-parameters';
import { ErrorAlert } from '../../../../../../shared/ui/error-alert/error-alert';
import { QuoteCard } from './quote-card/quote-card';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../../../../../core/services/language.service';

@Component({
  selector: 'app-customer-tab-quotes',
  imports: [
    NgClass,
    ModalFile,
    Pagination,
    FormsModule,
    Modal,
    QuotesFilters,
    LoadingSpinner,
    ErrorAlert,
    QuoteCard,
    TranslatePipe,
  ],
  templateUrl: './customer-tab-quotes.html',
  styleUrl: './customer-tab-quotes.css',
})
export class CustomerTabQuotes implements OnInit, OnDestroy {
  // Quotes
  public isQuotesLoading: boolean = false;
  public quotes: QuotePerCustomer[] = [];
  public errorMessage?: string = undefined;
  public quotesStatuses: string[] = [];
  public finalizedStatuses: string[] = [
    'accepted',
    'rejected',
    'cancelled',
    'expired',
    'converted',
  ];

  // PDF Modal
  public isModalOpen: boolean = false;
  public pdfUrl?: string = undefined;
  public isQuotePdfLoading: boolean = false;
  public quotesErrorMessage?: string = undefined;

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  private quotesToShow: number = 5;
  public pageSize: number = this.quotesToShow;

  // Quotes Status Update Modal
  public isQuotesStatusModalEnabled: boolean = false;
  public quoteStatusModalHeader?: string = undefined;
  public quoteStatusModalMessage?: string = undefined;
  public editingQuoteId?: number;
  public pendingStatus?: string;
  private quoteToBeUpdated?: QuotePerCustomer = undefined;
  private desiredQuoteStatus?: string = undefined;

  // Filters
  public activeSortLabel: string = 'Sort by';
  public activeFilterLabel: string = 'Filter by';
  private searchChanged$ = new Subject<string>();
  private currentParams: QuotesPerCustomerParameters = { page: 0 };

  // Services
  private customersService = inject(CustomersService);
  private quotesService = inject(QuotesService);
  private metadataService = inject(MetadataService);
  private languageService = inject(LanguageService);

  // Subscriptions
  private sub$?: Subscription;
  private quotesStatuses$?: Subscription;
  private quotesReqSub$?: Subscription;
  private subSearch$?: Subscription;
  private langChangeSub?: Subscription;

  // Lifecycle
  ngOnInit(): void {
    this.fetchQuotesPerCustomer({ size: this.quotesToShow });
    this.metadataService.fetchQuotesStatuses();
    this.subscribeToQuotesStatuses();
    this.setLabels();

    // Debouncer on search
    this.subSearch$ = this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((value) => this.onSearch(value));

    this.langChangeSub = this.languageService.onLangChange.subscribe(() => this.setLabels());
  }

  ngOnDestroy(): void {
    this.sub$?.unsubscribe();
    this.quotesStatuses$?.unsubscribe();
    this.subSearch$?.unsubscribe();
    this.quotesReqSub$?.unsubscribe();
    this.langChangeSub?.unsubscribe();
  }

  public onSearchChanged(value: string): void {
    this.searchChanged$.next(value);
  }

  /**
   * Opens the selected quote in PDF modal
   * @param id quote id
   */
  public openQuote(id: number): void {
    this.fetchQuote(id);
    this.isModalOpen = true;
  }

  /**
   * Close PDF modal on event
   */
  public closeQuote(): void {
    this.isModalOpen = false;
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.fetchQuotesPerCustomer({ page: page, size: this.quotesToShow });
  }

  /**
   * Updates the quote status on confirm click event
   * @returns
   */
  public onQuotesStatusModalConfirmClick(): void {
    if (!this.quoteToBeUpdated || !this.desiredQuoteStatus) return;

    this.isQuotesStatusModalEnabled = false;
    this.onStatusChange(this.quoteToBeUpdated, this.desiredQuoteStatus);
    this.resetQuoteStatusModalVariables();
  }

  /**
   * Close alert modal on cancel event,
   * when updating the quote status
   */
  public onQuotesStatusModalCancelClick(): void {
    this.isQuotesStatusModalEnabled = false;
    this.resetQuoteStatusModalVariables();
  }

  /**
   * Build and setup the alert modal before updating the quote status
   * @param quote selected quote
   * @param newStatus desired status to be updated
   */
  public handleQuotesStatusModal(quote: QuotePerCustomer, newStatus: string): void {
    this.quoteToBeUpdated = quote;
    this.desiredQuoteStatus = newStatus;
    this.editingQuoteId = quote.id;
    this.pendingStatus = newStatus;

    // Setup and enable modal
    this.quoteStatusModalHeader = this.languageService.translateKey(
      'customers.actions.update-quote-status',
    );
    this.quoteStatusModalMessage = this.languageService.translateKey(
      'customers.messages.quotes.status-modal-message',
      {
        number: quote.number,
        currentStatus: this.translateStatus(quote.status),
        newStatus: this.translateStatus(newStatus),
      },
    );
    this.isQuotesStatusModalEnabled = true;
  }

  /**
   * Refresh the quotes on refresh event
   */
  public onRefresh(): void {
    this.fetchQuotesPerCustomer({ size: this.quotesToShow });
  }

  /**
   * Sorts the quotes based on the selected query,
   * and updates the label to match the active query
   * @param query selected option to sort the data
   * @returns
   */
  public onSort(query: string) {
    if (!query) return;

    switch (query) {
      case 'sort-all':
        this.activeSortLabel = this.languageService.translateKey('common.filters.sort-by');
        this.fetchQuotesPerCustomer({ page: 0, sortBy: undefined, sortDirection: undefined });
        break;
      case 'sort-asc-by-number':
        this.activeSortLabel = `${this.languageService.translateKey('common.fields.number')} 0 → 9`;
        this.fetchQuotesPerCustomer({ page: 0, sortBy: 'number', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-number':
        this.activeSortLabel = `${this.languageService.translateKey('common.fields.number')} 9 → 0`;
        this.fetchQuotesPerCustomer({ page: 0, sortBy: 'number', sortDirection: 'desc' });
        break;
      case 'sort-asc-by-issue-date':
        this.activeSortLabel = `${this.languageService.translateKey('common.fields.date')} 0 → 9`;
        this.fetchQuotesPerCustomer({ page: 0, sortBy: 'issueDate', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-issue-date':
        this.activeSortLabel = `${this.languageService.translateKey('common.fields.date')} 9 → 0`;
        this.fetchQuotesPerCustomer({ page: 0, sortBy: 'issueDate', sortDirection: 'desc' });
        break;
    }
  }

  /**
   * Filters quotes based on the query,
   * and updates the label to match the active query
   * @param query selected option to filter quotes
   * @returns
   */
  public onFilter(query: string) {
    if (!query) return;

    switch (query) {
      case 'filter-by-all':
        this.activeFilterLabel = this.languageService.translateKey('common.filters.filter-by');
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: undefined, sortDirection: undefined });
        break;
      case 'filter-by-quote-status-draft':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.quotes-statuses.draft',
        );
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'DRAFT' });
        break;
      case 'filter-by-quote-status-sent':
        this.activeFilterLabel = this.languageService.translateKey('metadata.quotes-statuses.sent');
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'SENT' });
        break;
      case 'filter-by-quote-status-accepted':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.quotes-statuses.accepted',
        );
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'ACCEPTED' });
        break;
      case 'filter-by-quote-status-rejected':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.quotes-statuses.rejected',
        );
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'REJECTED' });
        break;
      case 'filter-by-quote-status-expired':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.quotes-statuses.expired',
        );
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'EXPIRED' });
        break;
      case 'filter-by-quote-status-cancelled':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.quotes-statuses.cancelled',
        );
        this.fetchQuotesPerCustomer({ page: 0, quoteStatus: 'CANCELLED' });
        break;
    }
  }

  /**
   * Gets all the quotes for the selected customer
   * Applies filtering and sorting if exist
   * @param params params to apply filtering & sorting
   */
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
          const data = res.data;
          this.quotes = data.content;

          this.currentPage = data.number;
          this.totalPages = data.totalPages;
          this.totalElements = data.totalElements;
        },
        error: (err) => {
          this.isQuotesLoading = false;
          if (err.status === 500) {
            this.errorMessage = 'common.errors.server';
          } else {
            this.errorMessage = 'common.errors.generic';
          }
        },
      });
    });
  }

  /**
   * Fetch a quote by its id
   * @param id quote id
   */
  private fetchQuote(id: number): void {
    this.isQuotePdfLoading = true;
    this.quotesErrorMessage = undefined;

    this.quotesService.fetchQuoteById(id).subscribe({
      next: (res) => {
        this.isQuotePdfLoading = false;
        this.pdfUrl = res.data.pdfUrl;
      },
      error: (err) => {
        this.isQuotePdfLoading = false;
        if (err.status === 404) {
          this.quotesErrorMessage = this.languageService.translateKey(
            'customers.messages.quotes.not-found-id',
            { id: id },
          );
        } else if (err.status === 500) {
          this.quotesErrorMessage = 'common.errors.server';
        } else {
          this.quotesErrorMessage = 'common.errors.generic';
        }
      },
    });
  }

  /**
   * Updates the selected quote status to the desired status
   * @param quote selected quote
   * @param newStatus desired status of the selected quote
   * @returns
   */
  private onStatusChange(quote: QuotePerCustomer, newStatus?: string): void {
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
        this.languageService.toastSuccess('customers.messages.quotes.success-status-update');
      },
      error: (err) => {
        // Reset to previous value
        quote.status = oldStatus;
        this.editingQuoteId = undefined;
        this.pendingStatus = undefined;

        let errorMessage = err.error.error;
        let currentStatus = errorMessage?.details?.currentStatus;
        let desiredStatus = errorMessage?.details?.desiredStatus;

        if (err.status === 409 && currentStatus && desiredStatus) {
          this.languageService.toastError('customers.messages.quotes.status-violation', {
            currentStatus: this.translateStatus(currentStatus),
            desiredStatus: this.translateStatus(desiredStatus),
          });

          return;
        }

        if (err.status === 404) {
          this.languageService.toastError('customers.messages.quotes.not-found');
        } else if (err.status === 500) {
          this.languageService.toastError('common.errors.server');
        } else {
          this.languageService.toastError('common.errors.generic');
        }
      },
    });
  }

  /**
   * Gets the quote statuses from the metadata service,
   * excluding EXPIRED status
   */
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

  /**
   * Resets variables of the current selected quote
   */
  private resetQuoteStatusModalVariables(): void {
    this.quoteToBeUpdated = undefined;
    this.desiredQuoteStatus = undefined;
    this.editingQuoteId = undefined;
    this.pendingStatus = undefined;
  }

  /**
   * Handles search input changes and filters by quotes number
   * Reset active filters & sorting when searching
   * @param value search input
   * @returns
   */
  private onSearch(value: string): void {
    let param = value.trim();

    if (param.length === 0) {
      this.fetchQuotesPerCustomer({ page: 0, size: this.quotesToShow, number: undefined });
      return;
    }

    this.activeFilterLabel = this.languageService.translateKey('common.filters.filter-by');
    this.activeSortLabel = this.languageService.translateKey('common.filters.sort-by');
    this.fetchQuotesPerCustomer({ size: this.quotesToShow, number: param });
  }

  private setLabels(): void {
    this.languageService
      .translateKeyAsync('common.filters.sort-by')
      .pipe(take(1))
      .subscribe((val) => (this.activeSortLabel = val));
    this.languageService
      .translateKeyAsync('common.filters.filter-by')
      .pipe(take(1))
      .subscribe((val) => (this.activeFilterLabel = val));
  }

  /**
   * Translates the status of Quote
   * @param status The status to be translated
   * @returns The translated status
   */
  private translateStatus(status: string): string {
    return this.languageService.translateKey(`metadata.quotes-statuses.${status.toLowerCase()}`);
  }
}
