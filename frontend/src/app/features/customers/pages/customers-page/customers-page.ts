import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CustomersService } from '../../customers.service';
import { Customer } from '../../models/customer';
import { CustomersTable } from '../../components/customers-table/customers-table';
import { CustomersFilters } from '../../components/customers-filters/customers-filters';
import { FetchCustomersParameters } from '../../models/fetch-customers-parameters';
import { debounceTime, distinctUntilChanged, forkJoin, Subject, Subscription, take } from 'rxjs';
import { Modal } from '../../../../shared/ui/modal/modal';
import { MetadataService } from '../../../../core/metadata/metadata.service';
import { HttpErrorResponse } from '@angular/common/http';
import { LanguageService } from '../../../../core/services/language.service';
import { TranslatePipe } from '@ngx-translate/core';
import { Page } from '../../../../shared/models/page.interface';
import { Pagination } from '../../../../shared/ui/pagination/pagination';

@Component({
  selector: 'app-customers-page',
  imports: [CustomersTable, CustomersFilters, Modal, TranslatePipe, Pagination],
  templateUrl: './customers-page.html',
  styleUrl: './customers-page.css',
})
export class CustomersPage implements OnInit, OnDestroy {
  private customersService: CustomersService = inject(CustomersService);
  private metadataService: MetadataService = inject(MetadataService);
  private languageService = inject(LanguageService);
  private searchChanged$ = new Subject<string>(); // Stream
  private currentParams: FetchCustomersParameters = {
    page: 0,
  };

  // Data and UI
  public isLoading: boolean = false;
  public customers: Customer[] = [];
  public errorMessage?: string = undefined;
  public disableDeleteButton: boolean = true;
  public selectCustomerIds = new Set<number>();

  // Modal
  public showModal: boolean = false;
  public modalHeader: string = '';
  public modalMessage: string = '';

  // Filters
  public isFilterActive: boolean = false;
  public customerTypes: string[] = [];
  public activeFilterLabel: string = '';
  public activeSortLabel: string = '';

  // Pagination
  public page: Page = { size: 0, number: 0, totalElements: 0, totalPages: 0 };

  private langChangeSub?: Subscription;

  // Lifecycle
  ngOnInit(): void {
    this.fetchCustomers();
    this.fetchCustomerTypes();
    this.setFilteringLabels();

    // Add debouncer to search bar
    this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((value) => this.onSearch(value));

    this.langChangeSub = this.languageService.onLangChange.subscribe(() =>
      this.setFilteringLabels(),
    );
  }

  ngOnDestroy(): void {
    this.langChangeSub?.unsubscribe();
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
    if (page === this.page.number) return;

    this.page.number = page;
    this.selectCustomerIds.clear();
    this.disableDeleteButton = true;
    this.fetchCustomers({ page: page });
  }

  public onSearchChanged(value: string): void {
    this.searchChanged$.next(value);
  }

  public onSearch(value: string): void {
    this.selectCustomerIds.clear();
    this.disableDeleteButton = true;

    const param = value.trim();
    const tinRegex = /^\d{9}$/; // Exactly 9 digits

    if (param.length === 0) {
      this.fetchCustomers({
        page: 0,
        tin: undefined,
        companyName: undefined,
        sortBy: undefined,
        sortDirection: undefined,
        customerType: undefined,
      });
      return;
    }

    this.isFilterActive = true;
    // Reset labels
    this.setFilteringLabels();

    if (tinRegex.test(param)) {
      this.fetchCustomers({
        page: 0,
        tin: param,
        companyName: undefined,
        sortBy: undefined,
        sortDirection: undefined,
        customerType: undefined,
      });
    } else if (param.length >= 3) {
      this.fetchCustomers({
        page: 0,
        tin: undefined,
        companyName: param,
        sortBy: undefined,
        sortDirection: undefined,
        customerType: undefined,
      });
    }
  }

  public onRefreshCustomers(): void {
    this.selectCustomerIds.clear();
    this.disableDeleteButton = true;
    this.fetchCustomers();
  }

  public onCustomerDeleteClick(): void {
    this.modalHeader = 'customers.messages.delete-customers-title';
    this.modalMessage = 'common.messages.cannot-undone';
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

  public onSort(query: string) {
    if (!query) return;

    this.selectCustomerIds.clear();
    this.disableDeleteButton = true;
    this.isFilterActive = true;

    switch (query) {
      case 'sort-all':
        this.activeSortLabel = this.languageService.translateKey('common.filters.sort-by');
        this.fetchCustomers({ page: 0, sortBy: undefined, sortDirection: undefined });
        break;
      case 'sort-asc-by-company-name':
        this.activeSortLabel = 'A → Z';
        this.fetchCustomers({ page: 0, sortBy: 'companyName', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-company-name':
        this.activeSortLabel = 'Z → A';
        this.fetchCustomers({ page: 0, sortBy: 'companyName', sortDirection: 'desc' });
        break;
    }
  }

  public onFilter(query: string) {
    if (!query) return;

    this.selectCustomerIds.clear();
    this.disableDeleteButton = true;
    this.isFilterActive = true;

    switch (query) {
      case 'filter-by-all':
        this.activeFilterLabel = this.languageService.translateKey('common.filters.filter-by');
        this.fetchCustomers({ page: 0, customerType: undefined });
        break;
      case 'filter-by-customer-type-company':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.customers-types.company',
        );
        this.fetchCustomers({ page: 0, customerType: 'COMPANY' });
        break;
      case 'filter-by-customer-type-individual':
        this.activeFilterLabel = this.languageService.translateKey(
          'metadata.customers-types.individual',
        );
        this.fetchCustomers({ page: 0, customerType: 'INDIVIDUAL' });
        break;
    }
  }

  // Helpers
  private fetchCustomers(params?: FetchCustomersParameters): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    // Merge current state params with new params
    const finalParams: FetchCustomersParameters = {
      ...this.currentParams,
      ...params,
    };

    // Saved for future requrests
    this.currentParams = finalParams;

    this.customersService.fetchCustomers(finalParams).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.selectCustomerIds.clear();
        const data = res.data;
        this.customers = data.content;
        this.page = data.page;
      },
      error: (err) => {
        this.isLoading = false;
        this.handleError(err);
      },
    });
  }

  private deleteCustomers(): void {
    if (this.selectCustomerIds.size === 0) return;
    this.isLoading = true;

    const ids = Array.from(this.selectCustomerIds);
    const deleteCustomers = ids.map((id) => this.customersService.deleteCustomer(id));

    forkJoin(deleteCustomers).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.languageService.toastSuccess('customers.messages.success-deletion');
        this.selectCustomerIds.clear();
        this.disableDeleteButton = true;
        this.fetchCustomers();
      },
      error: (err) => {
        this.isLoading = false;
        this.handleError(err);
      },
    });
  }

  private fetchCustomerTypes(): void {
    this.isLoading = true;

    this.metadataService.fetchCustomersTypes().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.customerTypes = res.data;
      },
      error: (err) => {
        this.isLoading = false;
        this.handleError(err);
      },
    });
  }

  // Helper method that handles error status from HTTP request
  private handleError(err: HttpErrorResponse): void {
    if (err.status === 500) {
      this.errorMessage = 'common.errors.server';
    } else {
      this.errorMessage = 'common.errors.generic';
    }
  }

  private setFilteringLabels(): void {
    // Translate
    this.languageService
      .translateKeyAsync('common.filters.filter-by')
      .pipe(take(1))
      .subscribe((val) => (this.activeFilterLabel = val));
    this.languageService
      .translateKeyAsync('common.filters.sort-by')
      .pipe(take(1))
      .subscribe((val) => (this.activeSortLabel = val));
  }
}
