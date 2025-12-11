import { Component, inject, OnInit } from '@angular/core';
import { QuotesService } from '../quotes.service';
import { QuotesTable } from '../quotes-table/quotes-table';
import { QuotesListItem } from '../models/quotes-list-item';
import { ModalFile } from '../../shared/ui/modal-file/modal-file';
import { QuotesFilters } from '../quotes-filters/quotes-filters';
import { FetchQuotesParameters } from '../models/fetch-quotes-parameters';
import { Pagination } from '../../shared/ui/pagination/pagination';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-quotes-page',
  imports: [QuotesTable, ModalFile, QuotesFilters, Pagination],
  templateUrl: './quotes-page.html',
  styleUrl: './quotes-page.css',
})
export class QuotesPage implements OnInit {
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
  public activeSortLabel: string = 'Sort by';

  public activeFilterLabel: string = 'Filter by';

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  public pageSize: number = 0;

  ngOnInit(): void {
    this.fetchQuotes();

    // Add debouncer to search bar
    this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((value) => this.onSearch(value));
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
        this.activeSortLabel = 'Sort by';
        this.fetchQuotes({ page: 0, sortBy: undefined, sortDirection: undefined });
        break;
      case 'sort-asc-by-number':
        this.activeSortLabel = 'Number 0 → 9';
        this.fetchQuotes({ page: 0, sortBy: 'number', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-number':
        this.activeSortLabel = 'Number 9 → 0';
        this.fetchQuotes({ page: 0, sortBy: 'number', sortDirection: 'desc' });
        break;
      case 'sort-asc-by-issue-date':
        this.activeSortLabel = 'Date 0 → 9';
        this.fetchQuotes({ page: 0, sortBy: 'issueDate', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-issue-date':
        this.activeSortLabel = 'Date 9 → 0';
        this.fetchQuotes({ page: 0, sortBy: 'issueDate', sortDirection: 'desc' });
        break;
    }
  }

  public onFilter(query: string) {
    if (!query) return;

    switch (query) {
      case 'filter-by-all':
        this.activeFilterLabel = 'Filter by';
        this.fetchQuotes({ page: 0, quoteStatus: undefined, sortDirection: undefined });
        break;
      case 'filter-by-quote-status-draft':
        this.activeFilterLabel = 'Draft';
        this.fetchQuotes({ page: 0, quoteStatus: 'DRAFT' });
        break;
      case 'filter-by-quote-status-sent':
        this.activeFilterLabel = 'Sent';
        this.fetchQuotes({ page: 0, quoteStatus: 'SENT' });
        break;
      case 'filter-by-quote-status-accepted':
        this.activeFilterLabel = 'Accepted';
        this.fetchQuotes({ page: 0, quoteStatus: 'ACCEPTED' });
        break;
      case 'filter-by-quote-status-expired':
        this.activeFilterLabel = 'Expired';
        this.fetchQuotes({ page: 0, quoteStatus: 'EXPIRED' });
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

    this.activeFilterLabel = 'Filter by';
    this.activeSortLabel = 'Sort by';

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
        this.quotes = res.content;

        // pagination
        this.currentPage = res.number;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
        this.pageSize = res.size;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occured. Please try again later';
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
        this.pdfUrl = res.pdfUrl;
        this.pdfNumber = res.number;
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 404) {
          this.errorMessage = `Quote with id ${id} not exist`;
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occured. Please try again later';
        }
      },
    });
  }
}
