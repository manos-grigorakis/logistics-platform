import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CmrDocument } from '../../models/cmr-document.interface';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { DatePipe, NgClass } from '@angular/common';
import { cmrDocumentStatusBadgeColor } from '../../../../shared/utils/cmr-document-status-badge-color.util';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cmr-documents-table',
  imports: [LoadingSpinner, TranslatePipe, DatePipe, NgClass, FormsModule],
  templateUrl: './cmr-documents-table.html',
  styleUrl: './cmr-documents-table.css',
})
export class CmrDocumentsTable {
  @Input() isLoading?: boolean;
  @Input() cmrDocuments?: CmrDocument[];
  @Input() cmrDocumentsStatuses?: string[];
  @Input() errorMessage?: string;
  @Output() onDocument = new EventEmitter<number>();
  @Output() onStatus = new EventEmitter<{ id: number; status: string }>();

  get filteredStatuses(): string[] {
    return this.cmrDocumentsStatuses?.filter((s) => s !== 'generated') ?? [];
  }

  public onDocumentClick(id: number): void {
    this.onDocument.emit(id);
  }

  public applyCmrDocumentBadgeColor(status: string): string {
    return cmrDocumentStatusBadgeColor(status);
  }

  public onSelectChange(id: number, event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.onStatusChange(id, value);
  }

  public onStatusChange(id: number, status: string): void {
    this.onStatus.emit({ id, status });
  }

  public showSignedText(value: boolean): string {
    return value ? 'common.messages.yes' : 'common.messages.no';
  }
}
