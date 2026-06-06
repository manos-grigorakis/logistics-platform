import { Component, inject, OnInit } from '@angular/core';
import { CustomersService } from '../customers.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Customer } from '../models/customer';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { CustomerHeader } from './customer-header/customer-header';
import { CustomerSidebar } from './customer-sidebar/customer-sidebar';
import { CustomerTabs } from './customer-tabs/customer-tabs';
import { LanguageService } from '../../shared/services/language.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-view-customer',
  imports: [LoadingSpinner, CustomerHeader, CustomerSidebar, CustomerTabs, TranslatePipe],
  templateUrl: './view-customer.html',
  styleUrl: './view-customer.css',
})
export class ViewCustomer implements OnInit {
  public isLoading: boolean = false;
  public customer?: Customer;
  public errorMessage?: string = undefined;

  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);
  private id: number = 0;

  // Services
  private customersService: CustomersService = inject(CustomersService);
  private languageService = inject(LanguageService);

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) {
      this.languageService.toastError('customers.messages.invalid-role-id');
      this.router.navigate(['/customers']);
      return;
    }

    this.id = parseInt(tempId);
    this.fetchCustomerById(this.id);
  }

  public handleTabClick(tab: string): void {
    switch (tab) {
      case 'quotes':
        this.router.navigate(['customers', 'view-customer', this.id, 'tab-quotes']);
        break;
    }
  }

  // Helper
  private fetchCustomerById(id: number): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.customersService.fetchCustomer(id).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.customer = res.data;
        this.customersService.setSelectedCustomer(this.customer);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.languageService.toastError('customers.messages.not-found-with-id', { id: this.id });
          this.router.navigate(['/customers']);
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }
}
