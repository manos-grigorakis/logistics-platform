import { Component } from '@angular/core';
import { ProgressBar } from '../../../../shared/ui/progress-bar/progress-bar';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-reconciliation-process-tab',
  imports: [ProgressBar, TranslatePipe],
  templateUrl: './reconciliation-process-tab.html',
  styleUrl: './reconciliation-process-tab.css',
})
export class ReconciliationProcessTab {}
