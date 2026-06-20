import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { SearchBar } from '../../../../shared/components/forms/search-bar/search-bar';
import { RoundedIconButton } from '../../../../shared/components/forms/rounded-icon-button/rounded-icon-button';
import { Router } from '@angular/router';
import { DropdownButton } from '../../../../shared/ui/dropdown-button/dropdown-button';
import { NgIcon } from '@ng-icons/core';
import { TranslatePipe } from '@ngx-translate/core';
import { SortOption } from '../../../../shared/models/sort-option.interface';

@Component({
  selector: 'app-suppliers-filters',
  imports: [SearchBar, RoundedIconButton, DropdownButton, NgIcon, TranslatePipe],
  templateUrl: './suppliers-filters.html',
  styleUrl: './suppliers-filters.css',
})
export class SuppliersFilters {
  @Input() isLoading?: boolean;
  @Input() isDeleteDisabled?: boolean;
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

  public loadSuppliers(): void {
    this.onRefresh.emit();
  }

  public onCreateSupplier(): void {
    this.router.navigate(['suppliers', 'create']);
  }

  public onSortByField(sortOption: SortOption | undefined): void {
    this.onSortBy.emit(sortOption);
  }
}
