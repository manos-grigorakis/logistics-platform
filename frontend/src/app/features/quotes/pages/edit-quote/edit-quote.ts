import { Component, inject, OnInit } from '@angular/core';
import { QuotesForm } from '../../components/quotes-form/quotes-form';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { QuotesService } from '../../quotes.service';
import { ActivatedRoute, Router } from '@angular/router';
import { QuoteRequest } from '../../models/quote-request';
import { Quote } from '../../models/quote';
import { QuoteFormPayload } from '../../models/quote-form-payload';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../../../core/services/language.service';

@Component({
  selector: 'app-edit-quote',
  imports: [QuotesForm, TranslatePipe],
  templateUrl: './edit-quote.html',
  styleUrl: './edit-quote.css',
})
export class EditQuote implements OnInit {
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;
  public quote!: Quote;

  private quoteId?: number;
  private userId?: number;

  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);

  // Services
  private authService: AuthService = inject(AuthService);
  private quotesService: QuotesService = inject(QuotesService);
  private languageService = inject(LanguageService);

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
      next: () => {
        this.isLoading = false;
        this.errorMessage = undefined;
        this.languageService.toastSuccess('quotes.messages.success-update');
        this.router.navigate(['quotes']);
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        if (err.status === 404) {
          this.errorMessage = 'quotes.messages.not-found-customer-or-user';
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
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
        this.quote = res.data;

        if (this.quote.quoteStatus !== 'draft') {
          this.languageService.toastWarning('quotes.messages.warning-draft-status-update');
          this.router.navigate(['quotes']);
          return;
        }
      },
      error: (err) => {
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
}
