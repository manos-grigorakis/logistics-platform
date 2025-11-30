import { Component, inject, OnInit } from '@angular/core';
import { CustomersService } from '../customers.service';
import { Customer } from '../models/customer';
import { CustomersTable } from '../customers-table/customers-table';
import { Pagination } from '../../shared/ui/pagination/pagination';

@Component({
  selector: 'app-customers-page',
  imports: [CustomersTable, Pagination],
  templateUrl: './customers-page.html',
  styleUrl: './customers-page.css',
})
export class CustomersPage implements OnInit {
  private customersService: CustomersService = inject(CustomersService);

  public isLoading: boolean = false;
  public customers: Customer[] = [];
  public errorMessage?: string = undefined;
  public selectCustomerIds = new Set<number>();

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  public pageSize: number = 0;

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

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.selectCustomerIds.clear();
    this.fetchCustomers(page);
  }

  private fetchCustomers(page?: number): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.customersService.fetchCustomers(page).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.customers = res.content;

        // pagination
        this.currentPage = res.number;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
        this.pageSize = res.size;
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
