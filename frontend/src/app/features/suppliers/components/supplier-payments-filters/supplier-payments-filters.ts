import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { SortOption } from '../../../../shared/models/sort-option.interface';
import { Router } from '@angular/router';
import { SearchBar } from '../../../../shared/components/forms/search-bar/search-bar';
import { TranslatePipe } from '@ngx-translate/core';
import { NgIcon } from '@ng-icons/core';
import { DropdownButton } from '../../../../shared/ui/dropdown-button/dropdown-button';
import { RoundedIconButton } from '../../../../shared/components/forms/rounded-icon-button/rounded-icon-button';

@Component({
  selector: 'app-supplier-payments-filters',
  imports: [SearchBar, TranslatePipe, NgIcon, DropdownButton, RoundedIconButton],
  templateUrl: './supplier-payments-filters.html',
  styleUrl: './supplier-payments-filters.css',
})
export class SupplierPaymentsFilters {
  @Input() isLoading?: boolean;
  @Input() sortLabel?: string;

  @Output() onSearchChanged = new EventEmitter<string>();
  @Output() onRefresh = new EventEmitter<void>();
  @Output() onSortBy = new EventEmitter<SortOption | undefined>();

  public searchTerm: string = '';

  private router = inject(Router);

  public onSearchChange(value: string): void {
    this.searchTerm = value;
    this.onSearchChanged.emit(value);
  }

  public loadSupplierPayments(): void {
    this.onRefresh.emit();
  }

  public onCreateSupplierPayment(): void {
    this.router.navigate(['suppliers', 'create-supplier-payment']);
  }

  public onSortByField(sortOption: SortOption | undefined): void {
    this.onSortBy.emit(sortOption);
  }
}
