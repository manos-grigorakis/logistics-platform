import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SearchBar } from '../../../../shared/components/forms/search-bar/search-bar';
import { RoundedIconButton } from '../../../../shared/components/forms/rounded-icon-button/rounded-icon-button';
import { DropdownButton } from '../../../../shared/ui/dropdown-button/dropdown-button';
import { NgIcon } from '@ng-icons/core';
import { TranslatePipe } from '@ngx-translate/core';
import { SortOption } from '../../../../shared/models/sort-option.interface';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-cmr-documents-filters',
  imports: [SearchBar, RoundedIconButton, DropdownButton, NgIcon, TranslatePipe, RouterLink],
  templateUrl: './cmr-documents-filters.html',
  styleUrl: './cmr-documents-filters.css',
})
export class CmrDocumentsFilters {
  @Input() isLoading?: boolean;
  @Input() searchPlaceholder: string = '';
  @Input() sortLabel?: string;
  @Input() filterLabel?: string;
  @Input() filterOptions: string[] = [];

  @Output() searchChanged = new EventEmitter<string>();
  @Output() onRefreshDocuments = new EventEmitter<void>();
  @Output() onSortByField = new EventEmitter<SortOption | undefined>();
  @Output() onFilterByField = new EventEmitter<string | undefined>();

  public searchTerm: string = '';

  public onSearchChange(value: string): void {
    this.searchTerm = value;
    this.searchChanged.emit(value);
  }

  public onRefreshDocumentsClick(): void {
    this.onRefreshDocuments.emit();
  }

  public onSortByFieldClick(sortOption: SortOption | undefined): void {
    this.onSortByField.emit(sortOption);
  }

  public onFilterByFieldClick(filterOption: string | undefined): void {
    this.onFilterByField.emit(filterOption);
  }
}
