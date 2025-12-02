import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Customer } from '../models/customer';
import { RouterLink } from '@angular/router';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { NgClass } from '@angular/common';
import { TitleCasePipe } from '@angular/common';
import { customerTypeBadgeColor } from '../utils/customer-type-color.utils';

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
    return customerTypeBadgeColor(type);
  }
}
