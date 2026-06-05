import { Component, inject, OnInit } from '@angular/core';
import { CustomersForm } from '../customers-form/customers-form';
import { CustomersService } from '../customers.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Customer } from '../models/customer';
import { CustomerRequest } from '../models/customer-request';
import { LanguageService } from '../../shared/services/language.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-edit-customer',
  imports: [CustomersForm, TranslatePipe],
  templateUrl: './edit-customer.html',
  styleUrl: './edit-customer.css',
})
export class EditCustomer implements OnInit {
  public isLoading: boolean = false;
  public errorMessage?: string;
  public customer?: Customer;

  private id: number = 0;
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);

  // Services
  private customerService: CustomersService = inject(CustomersService);
  private languageService = inject(LanguageService);

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) {
      this.languageService.toastError('customers.messages.invalid-role-id');
      this.router.navigate(['/roles']);
      return;
    }

    this.id = parseInt(tempId);
    this.fetchCustomerById(this.id);
  }

  public onSubmit(data: CustomerRequest) {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.customerService.updateCustomer(this.id, data).subscribe({
      next: () => {
        this.isLoading = false;
        this.errorMessage = undefined;
        this.languageService.toastSuccess('customers.messages.success-update');
        this.router.navigate(['/customers']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 409) {
          this.errorMessage = this.languageService.translateKey(
            'customers.messages.exist-by-company-name',
            { companyName: data.companyName },
          );
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }

  private fetchCustomerById(id: number): void {
    this.isLoading = true;

    this.customerService.fetchCustomer(id).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.errorMessage = undefined;

        this.customer = res.data;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.errorMessage = 'roles.messages.not-found';
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }
}
