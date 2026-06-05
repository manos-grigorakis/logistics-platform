import { Component, EventEmitter, Input, Output } from '@angular/core';
import { PrimaryButton } from '../../../shared/ui/primary-button/primary-button';
import { ReconciliationProcessResponse } from '../../models/reconciliation-process-response';
import { DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-reconciliation-results-tab',
  imports: [PrimaryButton, DatePipe, TranslatePipe],
  templateUrl: './reconciliation-results-tab.html',
  styleUrl: './reconciliation-results-tab.css',
})
export class ReconciliationResultsTab {
  @Input() results!: ReconciliationProcessResponse;
  @Output() newReconciliation = new EventEmitter<void>();

  public onNewReconciliation(): void {
    this.newReconciliation.emit();
  }

  public downloadReport(): void {
    const link = document.createElement('a');
    link.href = this.results.reportSummary.fileUrl;
    link.download = ''; // Force download
    link.click();
  }
}
