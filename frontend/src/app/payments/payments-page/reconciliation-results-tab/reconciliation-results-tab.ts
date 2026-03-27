import { Component, Input } from '@angular/core';
import { PrimaryButton } from '../../../shared/ui/primary-button/primary-button';
import { ReconciliationProcessResponse } from '../../models/reconciliation-process-response';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-reconciliation-results-tab',
  imports: [PrimaryButton, DatePipe],
  templateUrl: './reconciliation-results-tab.html',
  styleUrl: './reconciliation-results-tab.css',
})
export class ReconciliationResultsTab {
  @Input() results!: ReconciliationProcessResponse;

  public downloadReport(): void {
    const link = document.createElement('a');
    link.href = this.results.reportSummary.fileUrl;
    link.download = ''; // Force download
    link.click();
  }
}
