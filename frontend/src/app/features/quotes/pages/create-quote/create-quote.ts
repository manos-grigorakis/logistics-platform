import { Component, inject, OnInit } from '@angular/core';
import { QuotesForm } from '../../components/quotes-form/quotes-form';
import { QuotesService } from '../../quotes.service';
import { Router } from '@angular/router';
import { QuoteRequest } from '../../models/quote-request';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { LanguageService } from '../../../../core/services/language.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-create-quote',
  imports: [QuotesForm, TranslatePipe],
  templateUrl: './create-quote.html',
  styleUrl: './create-quote.css',
})
export class CreateQuote implements OnInit {
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  private userId?: number | null;
  private router: Router = inject(Router);

  // Services
  private quotesService: QuotesService = inject(QuotesService);
  private authService: AuthService = inject(AuthService);
  private languageService = inject(LanguageService);

  ngOnInit(): void {
    let tempId = this.authService.getUserId();

    if (!tempId) {
      this.router.navigate(['login']);
    }

    this.userId = tempId;
  }

  public onSubmit(data: any): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    if (!this.userId) return;

    const formattedData: QuoteRequest = {
      userId: this.userId,
      customerId: data.customerId,
      origin: data.origin,
      destination: data.destination,
      validityDays: data.validityDays,
      notes: data.notes,
      specialTerms: data.specialTerms,
      quoteItems: data.items,
    };

    this.quotesService.createQuote(formattedData).subscribe({
      next: () => {
        this.isLoading = false;
        this.errorMessage = undefined;
        this.languageService.toastSuccess('quotes.messages.success-creation');
        this.router.navigate(['quotes']);
      },
      error: (err) => {
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
}
