import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { QuotePerCustomerResponse } from '../../../models/quote-per-customer-response';
import { CustomersService } from '../../../customers.service';
import { Subscription } from 'rxjs';
import { NgClass, CurrencyPipe, DatePipe, TitleCasePipe } from '@angular/common';
import { ModalFile } from '../../../../shared/ui/modal-file/modal-file';
import { QuotesService } from '../../../../quotes/quotes.service';

@Component({
  selector: 'app-customer-tab-quotes',
  imports: [NgClass, CurrencyPipe, DatePipe, TitleCasePipe, ModalFile],
  templateUrl: './customer-tab-quotes.html',
  styleUrl: './customer-tab-quotes.css',
})
export class CustomerTabQuotes implements OnInit, OnDestroy {
  public isQuotesLoading: boolean = false;
  public isQuotePdfLoading: boolean = false;
  public quotes: QuotePerCustomerResponse[] = [];
  public errorMessage?: string = undefined;
  public quotesErrorMessage?: string = undefined;

  // PDF Modal
  public isModalOpen: boolean = false;
  public pdfUrl?: string = undefined;

  private customersService = inject(CustomersService);
  private quotesService = inject(QuotesService);
  private sub$?: Subscription;

  ngOnInit(): void {
    this.sub$ = this.customersService.selectedCustomer$.subscribe((c) => {
      if (!c) return;
      this.isQuotesLoading = true;
      this.errorMessage = undefined;

      this.customersService.quotesPerCustomer(c.id).subscribe({
        next: (res) => {
          this.isQuotesLoading = false;
          this.quotes = res;
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

  public quoteStatusBadgeColor(status: string): string {
    switch (status) {
      case 'DRAFT':
        return 'bg-secondary-100 text-secondary-800';
      case 'SENT':
        return 'bg-primary-100 text-primary-800';
      case 'ACCEPTED':
        return 'bg-success-light text-success-dark';
      case 'EXPIRED':
        return 'bg-warning-light text-warning-dark';
      default:
        return 'bg-slate-200 text-slate-700';
    }
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
