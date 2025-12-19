import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CustomersService } from '../../../customers.service';
import { Subscription } from 'rxjs';
import { NgClass, CurrencyPipe, DatePipe, TitleCasePipe } from '@angular/common';
import { ModalFile } from '../../../../shared/ui/modal-file/modal-file';
import { QuotesService } from '../../../../quotes/quotes.service';
import { quoteStatusBadgeColor } from '../../../../quotes/utils/quotes-status-badge-color';
import { QuotePerCustomer } from '../../../models/quotes-per-customer';
import { Pagination } from '../../../../shared/ui/pagination/pagination';

@Component({
  selector: 'app-customer-tab-quotes',
  imports: [NgClass, CurrencyPipe, DatePipe, TitleCasePipe, ModalFile, Pagination],
  templateUrl: './customer-tab-quotes.html',
  styleUrl: './customer-tab-quotes.css',
})
export class CustomerTabQuotes implements OnInit, OnDestroy {
  public isQuotesLoading: boolean = false;
  public isQuotePdfLoading: boolean = false;
  public quotes: QuotePerCustomer[] = [];
  public errorMessage?: string = undefined;
  public quotesErrorMessage?: string = undefined;

  // PDF Modal
  public isModalOpen: boolean = false;
  public pdfUrl?: string = undefined;

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  public pageSize: number = 0;
  private quotesToShow: number = 5;

  private customersService = inject(CustomersService);
  private quotesService = inject(QuotesService);
  private sub$?: Subscription;

  ngOnInit(): void {
    this.fetchQuotesPerCustomer(undefined, this.quotesToShow);
  }

  ngOnDestroy(): void {
    this.sub$?.unsubscribe();
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
    this.fetchQuotesPerCustomer(page, this.quotesToShow);
  }

  public quoteStatusBadgeColor(status: string): string {
    return quoteStatusBadgeColor(status);
  }

  private fetchQuotesPerCustomer(page?: number, size?: number): void {
    this.sub$ = this.customersService.selectedCustomer$.subscribe((c) => {
      if (!c) return;
      this.isQuotesLoading = true;
      this.errorMessage = undefined;

      this.customersService.quotesPerCustomer(c.id, page, size).subscribe({
        next: (res) => {
          this.isQuotesLoading = false;
          this.quotes = res.content;

          this.currentPage = res.number;
          this.totalPages = res.totalPages;
          this.totalElements = res.totalElements;
          this.pageSize = res.size;
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
}
