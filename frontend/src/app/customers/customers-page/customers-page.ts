import { Component, inject, OnInit } from '@angular/core';
import { CustomersService } from '../customers.service';
import { Customer } from '../models/customer';
import { CustomersTable } from '../customers-table/customers-table';
import { Pagination } from '../../shared/ui/pagination/pagination';
import { CustomersFilters } from '../customers-filters/customers-filters';
import { FetchCustomersParameters } from '../models/fetch-customers-parameters';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-customers-page',
  imports: [CustomersTable, Pagination, CustomersFilters],
  templateUrl: './customers-page.html',
  styleUrl: './customers-page.css',
})
export class CustomersPage implements OnInit {
  private customersService: CustomersService = inject(CustomersService);
  private searchChanged$ = new Subject<string>(); // Stream

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

    // Add debouncer to search bar
    this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((value) => this.onSearch(value));
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
    this.fetchCustomers({ page: page });
  }

  public onSearchChanged(value: string): void {
    this.searchChanged$.next(value);
  }

  public onSearch(value: string): void {
    const param = value.trim();
    const tinRegex = /^\d{9}$/; // Exactly 9 digits

    if (param.length === 0) {
      this.fetchCustomers();
      return;
    }

    if (tinRegex.test(param)) {
      this.fetchCustomers({ tin: param });
    } else if (param.length >= 3) {
      this.fetchCustomers({ companyName: param });
    }
  }

  public onRefreshCustomers(): void {
    this.fetchCustomers();
  }

  private fetchCustomers(params?: FetchCustomersParameters): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.customersService.fetchCustomers(params).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.selectCustomerIds.clear();
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
