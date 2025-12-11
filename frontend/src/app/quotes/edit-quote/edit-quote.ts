import { Component, inject, OnInit } from '@angular/core';
import { QuotesForm } from '../quotes-form/quotes-form';
import { AuthService } from '../../auth/services/auth.service';
import { QuotesService } from '../quotes.service';
import { ActivatedRoute, Router } from '@angular/router';
import { QuoteRequest } from '../models/quote-request';
import { Quote } from '../models/quote';
import { toast } from 'ngx-sonner';
import { QuoteFormPayload } from '../models/quote-form-payload';

@Component({
  selector: 'app-edit-quote',
  imports: [QuotesForm],
  templateUrl: './edit-quote.html',
  styleUrl: './edit-quote.css',
})
export class EditQuote implements OnInit {
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;
  public quote!: Quote;

  private authService: AuthService = inject(AuthService);
  private quotesService: QuotesService = inject(QuotesService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);

  private quoteId?: number;
  private userId?: number;

  ngOnInit(): void {
    let tempQuoteId = this.route.snapshot.paramMap.get('id');
    let tempUserId = this.authService.getUserId();

    if (!tempQuoteId || !tempUserId) return;

    this.quoteId = parseInt(tempQuoteId);
    this.userId = tempUserId;

    this.fetchQuoteById(this.quoteId);
  }

  public onSubmit(data: QuoteFormPayload): void {
    if (!this.quoteId || !this.userId) return;

    this.isLoading = true;
    this.errorMessage = undefined;

    const formattedData: QuoteRequest = {
      userId: this.userId,
      customerId: data.customerId,
      validityDays: data.validityDays,
      origin: data.origin,
      destination: data.destination,
      notes: data.notes,
      specialTerms: data.specialTerms,
      quoteItems: data.items,
    };

    this.quotesService.updateQuoteById(this.quoteId, formattedData).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.errorMessage = undefined;
        toast.success('Quote updated successfully');
        this.router.navigate(['quotes']);
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        if (err.status === 404) {
          this.errorMessage = 'Quote or Customer not found';
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }

  private fetchQuoteById(id: number): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.quotesService.fetchQuoteById(id).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.errorMessage = undefined;
        this.quote = res;
      },
      error: (err) => {
        if (err.status === 404) {
          this.errorMessage = `Quote not found with id: ${id}`;
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }
}
