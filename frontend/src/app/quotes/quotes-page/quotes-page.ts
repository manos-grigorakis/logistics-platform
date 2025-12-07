import { Component, inject, OnInit } from '@angular/core';
import { QuotesService } from '../quotes.service';
import { QuotesTable } from '../quotes-table/quotes-table';
import { QuotesListItem } from '../models/quotes-list-item';
import { ModalFile } from '../../shared/ui/modal-file/modal-file';

@Component({
  selector: 'app-quotes-page',
  imports: [QuotesTable, ModalFile],
  templateUrl: './quotes-page.html',
  styleUrl: './quotes-page.css',
})
export class QuotesPage implements OnInit {
  private quotesService: QuotesService = inject(QuotesService);

  public isLoading: boolean = false;
  public quotes: QuotesListItem[] = [];
  public errorMessage?: string = undefined;
  public pdfUrl?: string;
  public pdfNumber: string = '';
  public showModal: boolean = false;

  ngOnInit(): void {
    this.fetchQuotes();
  }

  public onViewQuoteClick(id: number) {
    this.viewQuote(id);
    this.showModal = true;
  }

  public closeModal(): void {
    this.showModal = false;
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
