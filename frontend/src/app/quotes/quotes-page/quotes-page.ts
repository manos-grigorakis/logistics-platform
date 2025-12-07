import { Component, inject, OnInit } from '@angular/core';
import { QuotesService } from '../quotes.service';
import { QuotesTable } from '../quotes-table/quotes-table';
import { QuotesListItem } from '../models/quotes-list-item';

@Component({
  selector: 'app-quotes-page',
  imports: [QuotesTable],
  templateUrl: './quotes-page.html',
  styleUrl: './quotes-page.css',
})
export class QuotesPage implements OnInit {
  private quotesService: QuotesService = inject(QuotesService);

  public isLoading: boolean = false;
  public quotes: QuotesListItem[] = [];
  public errorMessage?: string = undefined;

  ngOnInit(): void {
    this.fetchQuotes();
  }

  private fetchQuotes(): void {
    this.isLoading = true;

    this.quotesService.fetchQuotes().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.errorMessage = undefined;
        this.quotes = res.content;
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
}
