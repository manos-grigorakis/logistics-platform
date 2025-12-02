import { Component, inject } from '@angular/core';
import { CustomersForm } from '../customers-form/customers-form';
import { CustomersService } from '../customers.service';
import { Router } from '@angular/router';
import { CustomerRequest } from '../models/customer-request';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-create-customer',
  imports: [CustomersForm],
  templateUrl: './create-customer.html',
  styleUrl: './create-customer.css',
})
export class CreateCustomer {
  private customerService: CustomersService = inject(CustomersService);
  private router: Router = inject(Router);

  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  public onSubmit(data: CustomerRequest): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.customerService.createCustomer(data).subscribe({
      next: (res) => {
        this.isLoading = false;
        toast.success('Customer created successfully');
        this.router.navigate(['/customers']);
      },
      error: (err) => {
        this.isLoading = false;
        const fieldError = err.error?.details?.duplicateField;

        if (err.status === 409 && fieldError === 'tin') {
          this.errorMessage = `Customer with TIN: ${data.tin} already exists`;
        } else if (err.status === 409 && fieldError === 'companyName') {
          this.errorMessage = `Customer with Company Name: ${data.companyName} already exists`;
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }
}
