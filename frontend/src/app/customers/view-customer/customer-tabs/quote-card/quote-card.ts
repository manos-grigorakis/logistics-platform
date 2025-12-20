import { Component, EventEmitter, Input, Output } from '@angular/core';
import { QuotePerCustomer } from '../../../models/quotes-per-customer';
import { quoteStatusBadgeColor } from '../../../../quotes/utils/quotes-status-badge-color';
import { NgClass, TitleCasePipe, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-quote-card',
  imports: [NgClass, TitleCasePipe, CurrencyPipe, DatePipe, FormsModule],
  templateUrl: './quote-card.html',
  styleUrl: './quote-card.css',
})
export class QuoteCard {
  @Input() quote!: QuotePerCustomer;
  @Input() finalizedQuotesStatuses!: string[];
  @Input() pendingStatus?: string;
  @Input() editingQuoteId?: number;
  @Input() quotesStatuses!: string[];
  @Output() onViewQuote = new EventEmitter<number>();
  @Output() onStatus = new EventEmitter<{
    quote: QuotePerCustomer;
    newStatus: string;
  }>();

  public onViewQuoteClick(id: number): void {
    this.onViewQuote.emit(id);
  }

  public onStatusChange(quote: QuotePerCustomer, newStatus: string): void {
    this.onStatus.emit({ quote, newStatus });
  }

  /**
   * Applies CSS badge for quote status based on the status
   * @param status quote status
   * @returns applies CSS badge style for status
   */
  public quoteStatusBadgeColor(status: string): string {
    return quoteStatusBadgeColor(status);
  }
}
