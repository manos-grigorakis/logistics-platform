import { Component, inject, OnInit } from '@angular/core';
import { CustomersService } from '../customers.service';
import { Customer } from '../models/customer';
import { CustomersTable } from '../customers-table/customers-table';
import { Pagination } from '../../shared/ui/pagination/pagination';
import { CustomersFilters } from '../customers-filters/customers-filters';
import { FetchCustomersParameters } from '../models/fetch-customers-parameters';
import { debounceTime, distinctUntilChanged, forkJoin, Subject } from 'rxjs';
import { Modal } from '../../shared/ui/modal/modal';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-customers-page',
  imports: [CustomersTable, Pagination, CustomersFilters, Modal],
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
  public disableDeleteButton: boolean = true;
  public showModal: boolean = false;
  public modalHeader: string = '';
  public modalMessage: string = '';

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
    if (this.selectCustomerIds.has(customerId)) {
      this.selectCustomerIds.delete(customerId);
    } else {
      this.selectCustomerIds.add(customerId);
    }

    this.disableDeleteButton = this.selectCustomerIds.size === 0;
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.selectCustomerIds.clear();
    this.disableDeleteButton = true;
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

  public onCustomerDeleteClick(): void {
    this.modalHeader = 'Delete Selected Customer(s)';
    this.modalMessage = 'This action is permanent and cannot be undone';
    this.showModal = true;
  }

  public handleDelete(): void {
    this.deleteCustomers();
    this.disableDeleteButton = true;
    this.showModal = false;
  }

  public hideModal(): void {
    this.showModal = false;
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

  private deleteCustomers(): void {
    if (this.selectCustomerIds.size === 0) return;
    this.isLoading = false;

    const ids = Array.from(this.selectCustomerIds);
    const deleteCustomers = ids.map((id) => this.customersService.deleteCustomer(id));

    forkJoin(deleteCustomers).subscribe({
      next: (res) => {
        this.isLoading = false;
        toast.success('Customer(s) deleted successfully');
        this.selectCustomerIds.clear();
        this.disableDeleteButton = true;
        this.fetchCustomers();
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          toast.error('Server error. Please try again');
        } else {
          toast.error('An error has occured. Please try again');
        }
      },
    });
  }
}
