import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CustomersService } from '../../../customers.service';
import { filter, map, Subscription } from 'rxjs';
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
  public pageSize: number = 0;
  private quotesToShow: number = 5;

  // Quotes Status Modal
  public isQuotesStatusModalEnabled: boolean = false;
  public quoteStatusModalHeader?: string = undefined;
  public quoteStatusModalMessage?: string = undefined;

  private customersService = inject(CustomersService);
  private quotesService = inject(QuotesService);
  private metadataService = inject(MetadataService);

  private sub$?: Subscription;
  private quotesStatuses$?: Subscription;

  public editingQuoteId?: number;
  public pendingStatus?: string;
  private quoteToBeUpdated?: QuotePerCustomer = undefined;
  private desiredQuoteStatus?: string = undefined;

  ngOnInit(): void {
    this.fetchQuotesPerCustomer(undefined, this.quotesToShow);
    this.metadataService.fetchQuotesStatuses();
    this.subscribeToQuotesStatuses();
  }

  ngOnDestroy(): void {
    this.sub$?.unsubscribe();
    this.quotesStatuses$?.unsubscribe();
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
        toast.success('Quote status updated successfully');
      },
      error: (err) => {
        // Reset to previous value
        quote.status = oldStatus;

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

  private fetchQuotesPerCustomer(page?: number, size?: number): void {
    this.sub$?.unsubscribe();

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
}
