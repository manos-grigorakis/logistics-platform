import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CmrDocument } from '../../models/cmr-document.interface';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { DatePipe, NgClass } from '@angular/common';
import { cmrDocumentStatusBadgeColor } from '../../../../shared/utils/cmr-document-status-badge-color.util';

@Component({
  selector: 'app-cmr-documents-table',
  imports: [LoadingSpinner, TranslatePipe, DatePipe, NgClass],
  templateUrl: './cmr-documents-table.html',
  styleUrl: './cmr-documents-table.css',
})
export class CmrDocumentsTable {
  @Input() isLoading?: boolean;
  @Input() cmrDocuments?: CmrDocument[];
  @Input() errorMessage?: string;
  @Output() onDocument = new EventEmitter<number>();

  public onDocumentClick(id: number): void {
    this.onDocument.emit(id);
  }

  public applyCmrDocumentBadgeColor(status: string): string {
    return cmrDocumentStatusBadgeColor(status);
  }
}
