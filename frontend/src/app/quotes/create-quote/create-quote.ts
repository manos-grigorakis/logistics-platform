import { Component, inject, OnInit } from '@angular/core';
import { QuotesForm } from '../quotes-form/quotes-form';
import { QuotesService } from '../quotes.service';
import { Router } from '@angular/router';
import { toast } from 'ngx-sonner';
import { QuoteRequest } from '../models/quote-request';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-create-quote',
  imports: [QuotesForm],
  templateUrl: './create-quote.html',
  styleUrl: './create-quote.css',
})
export class CreateQuote implements OnInit {
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  private quotesService: QuotesService = inject(QuotesService);
  private authService: AuthService = inject(AuthService);
  private router: Router = inject(Router);
  private userId?: number | null;

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

    const formatedData: QuoteRequest = {
      userId: this.userId,
      customerId: data.customerId,
      origin: data.origin,
      destination: data.destination,
      validityDays: data.validityDays,
      notes: data.notes?.trim() === '' ? null : data.notes?.trim(),
      specialTerms: data.specialTerms?.trim() === '' ? null : data.specialTerms?.trim(),
      quoteItems: data.items,
    };

    this.quotesService.createQuote(formatedData).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.errorMessage = undefined;
        toast.success('Quote created successfully');
        this.router.navigate(['quotes']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 404) {
          this.errorMessage = 'Customer or user not found';
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An unexpected error occurred. Please try again';
        }
      },
    });
  }
}
