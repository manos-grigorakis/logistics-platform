import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { QuotesListItem } from '../models/quotes-list-item';
import { NgClass, DatePipe, TitleCasePipe } from '@angular/common';
import { quoteStatusBadgeColor } from '../utils/quotes-status-badge-color';

@Component({
  selector: 'app-quotes-table',
  imports: [LoadingSpinner, CurrencyPipe, RouterLink, DatePipe, TitleCasePipe, NgClass],
  templateUrl: './quotes-table.html',
  styleUrl: './quotes-table.css',
})
export class QuotesTable {
  @Input() isLoading?: boolean;
  @Input() quotes?: QuotesListItem[];
  @Input() errorMessage?: string;
  @Output() onViewQuote = new EventEmitter<number>();

  public onViewQuoteClick(id: number): void {
    this.onViewQuote.emit(id);
  }

  public quoteStatusBadgeColor(status: string): string {
    return quoteStatusBadgeColor(status);
  }
}
