import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Supplier } from '../../models/supplier.interface';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-suppliers-table',
  imports: [LoadingSpinner, TranslatePipe, CurrencyPipe, RouterLink],
  templateUrl: './suppliers-table.html',
  styleUrl: './suppliers-table.css',
})
export class SuppliersTable {
  @Input() isLoading?: boolean;
  @Input({ required: true }) suppliers?: Supplier[];

  @Output() onDelete = new EventEmitter<number>();

  public onDeleteClick(id: number): void {
    this.onDelete.emit(id);
  }
}
