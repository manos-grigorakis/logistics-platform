import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { QuotesService } from '../../quotes.service';
import { QuotesTable } from '../../components/quotes-table/quotes-table';
import { QuotesListItem } from '../../models/quotes-list-item';
import { ModalFile } from '../../../../shared/ui/modal-file/modal-file';
import { QuotesFilters } from '../../../../shared/components/quotes-filters/quotes-filters';
import { FetchQuotesParameters } from '../../models/fetch-quotes-parameters';
import { Pagination } from '../../../../shared/ui/pagination/pagination';
import { debounceTime, distinctUntilChanged, Subject, Subscription, take } from 'rxjs';
import { LanguageService } from '../../../../core/services/language.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-quotes-page',
  imports: [QuotesTable, ModalFile, QuotesFilters, Pagination, TranslatePipe],
  templateUrl: './quotes-page.html',
  styleUrl: './quotes-page.css',
})
export class QuotesPage implements OnInit, OnDestroy {
  private quotesService: QuotesService = inject(QuotesService);
  private searchChanged$ = new Subject<string>(); // Stream
  private currentParams: FetchQuotesParameters = {
    page: 0,
  };

  public isLoading: boolean = false;
  public quotes: QuotesListItem[] = [];
  public errorMessage?: string = undefined;
  public pdfUrl?: string;
  public pdfNumber: string = '';
  public showModal: boolean = false;

  // Filters
  public searchPlaceholder: string = '';
  public activeSortLabel: string = '';
  public activeFilterLabel: string = '';

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  public pageSize: number = 0;

  private languageService = inject(LanguageService);
  private langChangeSub?: Subscription;

  // Lifecycle
  ngOnInit(): void {
    this.fetchQuotes();

    // Add debouncer to search bar
    this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((value) => this.onSearch(value));

    this.setLabels();
    this.langChangeSub = this.languageService.onLangChange.subscribe(() => this.setLabels());
  }

  ngOnDestroy(): void {
    this.langChangeSub?.unsubscribe();
  }

  public onSearchChanged(value: string): void {
    this.searchChanged$.next(value);
  }

  public onViewQuoteClick(id: number) {
    this.viewQuote(id);
    this.showModal = true;
  }

  public closeModal(): void {
    this.showModal = false;
  }

  public onSort(query: string) {
    if (!query) return;

    switch (query) {
      case 'sort-all':
        this.activeSortLabel = this.languageService.translateKey('common.filters.sort-by');
        this.fetchQuotes({ page: 0, sortBy: undefined, sortDirection: undefined });
        break;
      case 'sort-asc-by-number':
        this.activeSortLabel = `${this.languageService.translateKey('common.fields.number')} 0 → 9`;
        this.fetchQuotes({ page: 0, sortBy: 'number', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-number':
        this.activeSortLabel = `${this.languageService.translateKey('common.fields.number')} 9 → 0`;
        this.fetchQuotes({ page: 0, sortBy: 'number', sortDirection: 'desc' });
        break;
      case 'sort-asc-by-issue-date':
        this.activeSortLabel = `${this.languageService.translateKey('common.fields.date')} 0 → 9`;
        this.fetchQuotes({ page: 0, sortBy: 'issueDate', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-issue-date':
        this.activeSortLabel = `${this.languageService.translateKey('common.fields.date')} 9 → 0`;
        this.fetchQuotes({ page: 0, sortBy: 'issueDate', sortDirection: 'desc' });
        break;
    }
  }

  public onFilter(query: string) {
    if (!query) return;

    switch (query) {
      case 'filter-by-all':
        this.activeFilterLabel = this.languageService.translateKey('common.filters.filter-by');
        this.fetchQuotes({ page: 0, quoteStatus: undefined, sortDirection: undefined });
        break;
      case 'filter-by-quote-status-draft':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.quotes-statuses.draft',
        );
        this.fetchQuotes({ page: 0, quoteStatus: 'DRAFT' });
        break;
      case 'filter-by-quote-status-sent':
        this.activeFilterLabel = this.languageService.translateKey('metadata.quotes-statuses.sent');
        this.fetchQuotes({ page: 0, quoteStatus: 'SENT' });
        break;
      case 'filter-by-quote-status-accepted':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.quotes-statuses.accepted',
        );
        this.fetchQuotes({ page: 0, quoteStatus: 'ACCEPTED' });
        break;
      case 'filter-by-quote-status-rejected':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.quotes-statuses.rejected',
        );
        this.fetchQuotes({ page: 0, quoteStatus: 'REJECTED' });
        break;
      case 'filter-by-quote-status-expired':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.quotes-statuses.expired',
        );
        this.fetchQuotes({ page: 0, quoteStatus: 'EXPIRED' });
        break;
      case 'filter-by-quote-status-cancelled':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.quotes-statuses.cancelled',
        );
        this.fetchQuotes({ page: 0, quoteStatus: 'CANCELLED' });
        break;
    }
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.fetchQuotes({ page: page });
  }

  public onRefresh(): void {
    this.fetchQuotes();
  }

  public onSearch(value: string): void {
    let param = value.trim();

    if (param.length === 0) {
      this.fetchQuotes({
        page: 0,
        number: undefined,
        companyName: undefined,
        sortBy: undefined,
        sortDirection: undefined,
        quoteStatus: undefined,
      });
      return;
    }

    this.setLabels();

    const minSearchByNumber = 5;
    const normalized = param.toUpperCase();
    const quoteMatch = normalized.startsWith('Q-');

    if (quoteMatch && param.length < minSearchByNumber) return;

    if (quoteMatch) {
      this.fetchQuotes({
        page: 0,
        number: normalized,
        companyName: undefined,
        sortBy: undefined,
        sortDirection: undefined,
        quoteStatus: undefined,
      });
    } else {
      this.fetchQuotes({
        page: 0,
        number: undefined,
        companyName: param,
        sortBy: undefined,
        sortDirection: undefined,
        quoteStatus: undefined,
      });
    }
  }

  private fetchQuotes(params?: FetchQuotesParameters): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    // Merge current state params with new params
    const finalParams: FetchQuotesParameters = {
      ...this.currentParams,
      ...params,
    };

    // Saved for future requrests
    this.currentParams = finalParams;

    this.quotesService.fetchQuotes(finalParams).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.errorMessage = undefined;
        const data = res.data;
        this.quotes = data.content;

        // pagination
        this.currentPage = data.number;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.pageSize = data.size;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }

  private viewQuote(id: number): void {
    this.isLoading = false;
    this.errorMessage = undefined;

    this.quotesService.fetchQuoteById(id).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.errorMessage = undefined;
        this.pdfUrl = res.data.pdfUrl;
        this.pdfNumber = res.data.number;
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 404) {
          this.errorMessage = this.languageService.translateKey(
            'quotes.messages.not-found-with-id',
            { id: id },
          );
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }

  private setLabels(): void {
    this.languageService
      .translateKeyAsync('quotes.filters.search-by-number-or-company-name')
      .pipe(take(1))
      .subscribe((val) => (this.searchPlaceholder = val));

    this.languageService
      .translateKeyAsync('common.filters.sort-by')
      .pipe(take(1))
      .subscribe((val) => (this.activeSortLabel = val));
    this.languageService
      .translateKeyAsync('common.filters.filter-by')
      .pipe(take(1))
      .subscribe((val) => (this.activeFilterLabel = val));
  }
}
