import { Component, inject } from '@angular/core';
import { CustomersForm } from '../customers-form/customers-form';
import { CustomersService } from '../customers.service';
import { Router } from '@angular/router';
import { CustomerRequest } from '../models/customer-request';
import { LanguageService } from '../../core/services/language.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-create-customer',
  imports: [CustomersForm, TranslatePipe],
  templateUrl: './create-customer.html',
  styleUrl: './create-customer.css',
})
export class CreateCustomer {
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  private router: Router = inject(Router);

  // Services
  private customerService: CustomersService = inject(CustomersService);
  private languageService = inject(LanguageService);

  public onSubmit(data: CustomerRequest): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.customerService.createCustomer(data).subscribe({
      next: () => {
        this.isLoading = false;
        this.languageService.toastSuccess('customers.messages.success-creation');
        this.router.navigate(['/customers']);
      },
      error: (err) => {
        this.isLoading = false;
        const fieldError = err.error?.error?.details?.duplicateField;

        if (err.status === 409 && fieldError === 'tin') {
          this.errorMessage = this.languageService.translateKey('customers.messages.exist-by-tin', {
            tin: data.tin,
          });
        } else if (err.status === 409 && fieldError === 'companyName') {
          this.errorMessage = this.languageService.translateKey(
            'customers.messages.exist-by-company-name',
            {
              companyName: data.companyName,
            },
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
