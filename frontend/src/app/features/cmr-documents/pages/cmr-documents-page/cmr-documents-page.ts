import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CmrDocumentsService } from '../../cmr-documents.service';
import { CmrDocument } from '../../models/cmr-document.interface';
import { CmrDocumentsTable } from '../../components/cmr-documents-table/cmr-documents-table';
import { Pagination } from '../../../../shared/ui/pagination/pagination';
import { CmrDocumentFilterRequest } from '../../models/cmr-document-filter-request.interface';
import { debounceTime, distinctUntilChanged, finalize, Subject, Subscription, take } from 'rxjs';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { TranslatePipe } from '@ngx-translate/core';
import { ModalFile } from '../../../../shared/ui/modal-file/modal-file';
import { LanguageService } from '../../../../core/services/language.service';
import { CmrDocumentsFilters } from '../../components/cmr-documents-filters/cmr-documents-filters';
import { SortOption } from '../../../../shared/models/sort-option.interface';

@Component({
  selector: 'app-cmr-documents-page',
  imports: [CmrDocumentsTable, Pagination, TranslatePipe, ModalFile, CmrDocumentsFilters],
  templateUrl: './cmr-documents-page.html',
  styleUrl: './cmr-documents-page.css',
})
export class CmrDocumentsPage implements OnInit, OnDestroy {
  public documents: CmrDocument[] = [];

  // UI
  public isLoading: boolean = false;
  public errorMessage: string | null = null;

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  public pageSize: number = 0;

  // Modal
  public isModalOpen: boolean = false;
  public documentUrl?: string;
  public documentNumber: string = '';

  // Searching
  public searchPlaceholder: string = '';
  private searchChanged$ = new Subject<string>();

  // Sorting
  public activeSortLabel: string = '';

  // Filtering
  public activeFilterLabel: string = '';
  public filterOptions: string[] = ['generated', 'signed', 'cancelled'];

  // Subscriptions
  private langChangeSub?: Subscription;

  private currentParams: CmrDocumentFilterRequest = { page: 0 };

  // Services
  private cmrDocumentService = inject(CmrDocumentsService);
  private languageService = inject(LanguageService);

  // Lifecycle
  ngOnInit(): void {
    this.fetchCmrDocuments();

    // Debouncer on search
    this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((val) => this.onSearch(val));

    this.setLabels();

    this.langChangeSub = this.languageService.onLangChange.subscribe(() => this.setLabels());
  }

  ngOnDestroy(): void {
    this.langChangeSub?.unsubscribe();
  }

  public onSearchChanged(value: string): void {
    this.searchChanged$.next(value);
  }

  public onSearch(value: string): void {
    let val = value.trim();

    if (val.length === 0) {
      return this.fetchCmrDocuments({
        page: 0,
        number: undefined,
      });
    }

    const minSearch = 7;
    const normalized = val.toUpperCase();
    const documentMatch = normalized.startsWith('CMR-');

    if (documentMatch && val.length < minSearch) return;

    this.fetchCmrDocuments({
      page: 0,
      number: normalized,
    });
  }

  public onRefreshClick(): void {
    this.fetchCmrDocuments();
  }

  public onSortByField(option: SortOption | undefined): void {
    this.fetchCmrDocuments({ sortBy: option?.sortBy, sortDirection: option?.sortDirection });

    if (option === undefined) {
      this.activeSortLabel = this.languageService.translateKey('common.filters.sort-by');
    } else {
      let directionLabel: string = '';
      if (option?.sortDirection === 'asc') directionLabel = '(0-9)';
      else directionLabel = '(9-0)';

      this.activeSortLabel = `${this.languageService.translateKey(`common.fields.${option.label}`)} ${directionLabel}`;
    }
  }

  public onFilterByField(option: string | undefined): void {
    this.fetchCmrDocuments({ status: option });

    if (option === undefined) {
      this.activeFilterLabel = this.languageService.translateKey('common.filters.filter-by');
    } else {
      this.activeFilterLabel = this.languageService.translateKey(
        `metadata.cmr-document-statuses.${option.toLowerCase()}`,
      );
    }
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.fetchCmrDocuments({ page: page });
  }

  public onDocumentClick(id: number): void {
    this.fetchCmrDocument(id);
    this.isModalOpen = true;
  }

  public onStatusUpdateChange(event: { id: number; status: string }): void {
    this.updateDocumentStatus(event.id, event.status);
  }

  private fetchCmrDocuments(params: CmrDocumentFilterRequest = {}): void {
    this.isLoading = true;
    this.errorMessage = null;

    // Merge current state params with new params
    const finalParams: CmrDocumentFilterRequest = {
      ...this.currentParams,
      ...params,
    };

    // Saved for future requests
    this.currentParams = finalParams;

    this.cmrDocumentService
      .fetchCmrDocuments(finalParams)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          const data = res.data;
          this.documents = data.content;

          // pagination
          this.currentPage = data.number;
          this.totalPages = data.totalPages;
          this.totalElements = data.totalElements;
          this.pageSize = data.size;
        },
        error: (err) => (this.errorMessage = handleHttpErrors(err.status)),
      });
  }

  private fetchCmrDocument(id: number): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.cmrDocumentService
      .fetchCmrDocument(id)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (res) => {
          this.errorMessage = null;
          const data = res.data;
          this.documentNumber = data.number;
          this.documentUrl = data.fileUrl;
        },
        error: (err) => {
          if (err.status === 404) {
            this.languageService.toastError('cmr-documents.messages.not-found');
          }

          this.errorMessage = handleHttpErrors(err.status);
        },
      });
  }

  private updateDocumentStatus(id: number, status: string): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.cmrDocumentService
      .updateCmrDocumentStatus(id, status)
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.fetchCmrDocuments();
        }),
      )
      .subscribe({
        next: () => {
          this.errorMessage = null;
          this.languageService.toastSuccess('cmr-documents.messages.status-update-success');
        },
        error: (err) => {
          const status = err.status;

          if (status === 404) {
            this.languageService.toastError('cmr-documents.messages.not-found');
          } else if (status === 409 && err.error.error.errorCode === 'NOT_SIGNED') {
            this.languageService.toastError('cmr-documents.messages.status-update-not-signed');
          } else if (status === 409) {
            this.languageService.toastError('cmr-documents.messages.status-update-violation');
          } else {
            this.languageService.toastError(handleHttpErrors(status));
          }
        },
      });
  }

  private setLabels(): void {
    this.languageService
      .translateKeyAsync('common.filters.search-by-number')
      .pipe(take(1))
      .subscribe((val) => (this.searchPlaceholder = val));

    this.languageService
      .translateKeyAsync('common.filters.sort-by')
      .pipe(take(1))
      .subscribe((val) => (this.activeSortLabel = val));

    this.languageService
      .translateKeyAsync('common.filters.filter-by')
      .pipe(take(1))
      .subscribe((val) => (this.activeFilterLabel = val));
  }
}
