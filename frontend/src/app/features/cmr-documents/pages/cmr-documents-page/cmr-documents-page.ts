import { Component, inject, OnInit } from '@angular/core';
import { CmrDocumentsService } from '../../cmr-documents.service';
import { CmrDocument } from '../../models/cmr-document.interface';
import { CmrDocumentsTable } from '../../components/cmr-documents-table/cmr-documents-table';
import { Pagination } from '../../../../shared/ui/pagination/pagination';
import { CmrDocumentFilterRequest } from '../../models/cmr-document-filter-request.interface';
import { finalize } from 'rxjs';
import { handleHttpErrors } from '../../../../shared/utils/handle-http-errors.util';
import { TranslatePipe } from '@ngx-translate/core';
import { ModalFile } from '../../../../shared/ui/modal-file/modal-file';
import { LanguageService } from '../../../../core/services/language.service';

@Component({
  selector: 'app-cmr-documents-page',
  imports: [CmrDocumentsTable, Pagination, TranslatePipe, ModalFile],
  templateUrl: './cmr-documents-page.html',
  styleUrl: './cmr-documents-page.css',
})
export class CmrDocumentsPage implements OnInit {
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

  private currentParams: CmrDocumentFilterRequest = { page: 0 };

  // Services
  private cmrDocumentService = inject(CmrDocumentsService);
  private languageService = inject(LanguageService);

  ngOnInit(): void {
    this.fetchCmrDocuments();
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
}
