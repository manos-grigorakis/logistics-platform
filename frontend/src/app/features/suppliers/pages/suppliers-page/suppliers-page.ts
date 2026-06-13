import { Component, inject, OnInit } from '@angular/core';
import { SuppliersService } from '../../services/suppliers.service';
import { Supplier } from '../../models/supplier.interface';
import { LanguageService } from '../../../../core/services/language.service';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { finalize } from 'rxjs';
import { Pagination } from '../../../../shared/ui/pagination/pagination';
import { SuppliersTable } from '../../components/suppliers-table/suppliers-table';
import { FetchSuppliersParams } from '../../models/fetch-suppliers-params.interface';

@Component({
  selector: 'app-suppliers-page',
  imports: [Pagination, SuppliersTable],
  templateUrl: './suppliers-page.html',
  styleUrl: './suppliers-page.css',
})
export class SuppliersPage implements OnInit {
  public suppliers: Supplier[] = [];

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  public pageSize: number = 0;

  // UI
  public isLoading: boolean = false;

  // Services
  private suppliersService = inject(SuppliersService);
  private languageService = inject(LanguageService);

  private currentParams: FetchSuppliersParams = { page: 0 };

  ngOnInit(): void {
    this.fetchSuppliers();
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.fetchSuppliers({ page: page });
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
}
