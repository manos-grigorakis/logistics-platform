import { Component, inject, OnInit } from '@angular/core';
import { CustomersService } from '../customers.service';
import { Customer } from '../models/customer';
import { CustomersTable } from '../customers-table/customers-table';

@Component({
  selector: 'app-customers-page',
  imports: [CustomersTable],
  templateUrl: './customers-page.html',
  styleUrl: './customers-page.css',
})
export class CustomersPage implements OnInit {
  private customersService: CustomersService = inject(CustomersService);

  public isLoading: boolean = false;
  public customers: Customer[] = [];
  public errorMessage?: string = undefined;
  public selectCustomerIds = new Set<number>();

  ngOnInit(): void {
    this.fetchCustomers();
  }

  public toggleCustomerSelection(customerId: number): void {
    console.log(customerId);
    if (this.selectCustomerIds.has(customerId)) {
      this.selectCustomerIds.delete(customerId);
    } else {
      this.selectCustomerIds.add(customerId);
    }
  }

  private fetchCustomers(): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.customersService.fetchCustomers().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.customers = res.content;
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
}
