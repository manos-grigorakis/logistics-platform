import { Component, inject, OnInit } from '@angular/core';
import { CustomersForm } from '../customers-form/customers-form';
import { CustomersService } from '../customers.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Customer } from '../models/customer';
import { toast } from 'ngx-sonner';
import { CustomerRequest } from '../models/customer-request';

@Component({
  selector: 'app-edit-customer',
  imports: [CustomersForm],
  templateUrl: './edit-customer.html',
  styleUrl: './edit-customer.css',
})
export class EditCustomer implements OnInit {
  private customerService: CustomersService = inject(CustomersService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);
  private id: number = 0;

  public isLoading: boolean = false;
  public errorMessage?: string;
  public customer?: Customer;

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) {
      toast.error('Invalid role id');
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
      next: (res) => {
        this.isLoading = false;
        this.errorMessage = undefined;
        toast.success('Customer updated successfully');
        this.router.navigate(['/customers']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 409) {
          this.errorMessage = `Customer already exists with company name ${data.companyName}`;
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
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

        this.customer = res;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.errorMessage = 'Role not found';
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error has occured. Please try again';
        }
      },
    });
  }
}
