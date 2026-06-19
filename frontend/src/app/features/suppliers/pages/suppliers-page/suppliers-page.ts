import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { SuppliersService } from '../../services/suppliers.service';
import { Supplier } from '../../models/supplier.interface';
import { LanguageService } from '../../../../core/services/language.service';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { debounceTime, distinctUntilChanged, finalize, Subject, Subscription, take } from 'rxjs';
import { Pagination } from '../../../../shared/ui/pagination/pagination';
import { SuppliersTable } from '../../components/suppliers-table/suppliers-table';
import { FetchSuppliersParams } from '../../models/fetch-suppliers-params.interface';
import { SuppliersFilters } from '../../components/suppliers-filters/suppliers-filters';
import { SortOption } from '../../../../shared/models/sort-option.interface';
import { Modal } from '../../../../shared/ui/modal/modal';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-suppliers-page',
  imports: [Pagination, SuppliersTable, SuppliersFilters, Modal, TranslatePipe],
  templateUrl: './suppliers-page.html',
  styleUrl: './suppliers-page.css',
})
export class SuppliersPage implements OnInit, OnDestroy {
  public suppliers: Supplier[] = [];

  // Search
  private searchChanged$ = new Subject<string>();

  // Sorting
  public activeSortLabel: string = '';

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  public pageSize: number = 0;

  // UI
  public isLoading: boolean = false;

  // Modal
  public showModal: boolean = false;
  public modalHeader: string = '';
  public modalMessage: string = '';
  private selectedSupplierForDeletion: number | null = null;

  // Services
  private suppliersService = inject(SuppliersService);
  private languageService = inject(LanguageService);

  private currentParams: FetchSuppliersParams = { page: 0 };

  // Subscriptions
  private langChangeSub?: Subscription;

  // Lifecycle
  ngOnInit(): void {
    this.fetchSuppliers();

    // Debouncer on search
    this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((val) => this.onSearch(val));

    this.langChangeSub = this.languageService.onLangChange.subscribe(() =>
      this.languageService
        .translateKeyAsync('common.filters.sort-by')
        .pipe(take(1))
        .subscribe((val) => (this.activeSortLabel = val)),
    );
  }

  ngOnDestroy(): void {
    this.langChangeSub?.unsubscribe();
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.fetchSuppliers({ page: page });
  }

  public onSearchChanged(value: string) {
    this.searchChanged$.next(value);
  }

  public onSearch(keyword: string): void {
    if (keyword.trim().length === 0) {
      return this.fetchSuppliers({ page: 0, companyName: undefined });
    }

    this.fetchSuppliers({ page: 0, companyName: keyword.trim() });
  }

  public onRefresh(): void {
    this.fetchSuppliers({ page: 0 });
  }

  public onSortByField(option: SortOption | undefined): void {
    this.fetchSuppliers({ sortBy: option?.sortBy, sortDirection: option?.sortDirection });

    if (option === undefined) {
      this.activeSortLabel = this.languageService.translateKey('common.filters.sort-by');
    } else {
      let directionLabel: string = '';

      if (option?.sortDirection === 'asc') {
        if (!option?.isNumeric) directionLabel = '(A-Z)';
        else directionLabel = '(0-9)';
      } else {
        if (!option?.isNumeric) directionLabel = '(Z-A)';
        else directionLabel = '(9-0)';
      }

      this.activeSortLabel = `${this.languageService.translateKey(`common.fields.${option.label}`)} ${directionLabel}`;
    }
  }

  public onDeleteClick(id: number): void {
    this.selectedSupplierForDeletion = id;
    this.modalHeader = 'suppliers.messages.delete-supplier-title';
    this.modalMessage = 'common.messages.cannot-undone';
    this.showModal = true;
  }

  public handleDelete(): void {
    if (this.selectedSupplierForDeletion !== null) {
      this.deleteSelectedSupplier(this.selectedSupplierForDeletion);
      this.closeModalAndResetSelectedSupplier();
    }
  }

  public closeModalAndResetSelectedSupplier(): void {
    this.showModal = false;
    this.selectedSupplierForDeletion = null;
  }

  private fetchSuppliers(params?: FetchSuppliersParams): void {
    this.isLoading = true;

    // Merge current state params with new params
    const finalParams: FetchSuppliersParams = {
      ...this.currentParams,
      ...params,
    };

    // Saved for future requests
    this.currentParams = finalParams;

    this.suppliersService
      .fetchSuppliers(finalParams)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          const data = res.data;

          this.suppliers = data.content;

          // pagination
          this.currentPage = data.number;
          this.totalPages = data.totalPages;
          this.totalElements = data.totalElements;
          this.pageSize = data.size;
        },
        error: (err) => this.languageService.toastError(handleHttpErrors(err.status)),
      });
  }

  private deleteSelectedSupplier(id: number): void {
    this.isLoading = true;

    this.suppliersService
      .deactivateSupplierById(id)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          // Update UI
          this.suppliers = this.suppliers.filter((s) => s.id !== id);
          this.languageService.toastSuccess('suppliers.messages.success-deletion');
        },
        error: (err) => {
          if (err.status === 404) {
            this.languageService.toastError('suppliers.messages.not-found');
          } else {
            this.languageService.toastError(handleHttpErrors(err.status));
          }
        },
      });
  }
}
