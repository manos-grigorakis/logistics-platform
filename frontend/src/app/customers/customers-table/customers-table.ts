import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Customer } from '../models/customer';
import { RouterLink } from '@angular/router';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { NgClass } from '@angular/common';
import { TitleCasePipe } from '@angular/common';

@Component({
  selector: 'app-customers-table',
  imports: [RouterLink, LoadingSpinner, NgClass, TitleCasePipe],
  templateUrl: './customers-table.html',
  styleUrl: './customers-table.css',
})
export class CustomersTable {
  @Input() isLoading?: boolean;
  @Input() customers?: Customer[];
  @Input() errorMessage?: string;
  @Input() selectedCustomersIds = new Set<number>();
  @Output() toggleCustomerSelection = new EventEmitter<number>();

  public onRowClick(customerId: number): void {
    this.toggleCustomerSelection.emit(customerId);
  }

  public onCheckboxClick(event: MouseEvent, customerId: number): void {
    event.stopPropagation();
    this.toggleCustomerSelection.emit(customerId);
  }

  public customerTypeBadgeColor(type: string): string {
    switch (type) {
      case 'COMPANY':
        return 'bg-primary-100 text-primary-800';
      case 'INDIVIDUAL':
        return 'bg-success-light text-success-dark';
      default:
        return 'bg-secondary-100 text-secondary-800';
    }
  }
}
