import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { RoundedIconButton } from '../../shared/forms/rounded-icon-button/rounded-icon-button';
import { DropdownButton } from '../../shared/ui/dropdown-button/dropdown-button';
import { NgIcon } from '@ng-icons/core';
import { Router } from '@angular/router';
import { SearchBar } from '../../shared/forms/search-bar/search-bar';
import { TitleCasePipe } from '@angular/common';

@Component({
  selector: 'app-customers-filters',
  imports: [RoundedIconButton, DropdownButton, NgIcon, SearchBar, TitleCasePipe],
  templateUrl: './customers-filters.html',
  styleUrl: './customers-filters.css',
})
export class CustomersFilters {
  @Output() refresh = new EventEmitter<void>();
  @Output() deleteClick = new EventEmitter<void>();
  @Output() searchChanged = new EventEmitter<string>();
  @Output() sortBy = new EventEmitter<string>();
  @Output() filterBy = new EventEmitter<string>();
  @Input() isLoading: boolean = false;
  @Input() isDeleteDisabled?: boolean;
  @Input() customerTypesData?: string[];
  @Input() filterLabel?: string;
  @Input() sortLabel?: string;

  private router: Router = inject(Router);

  public searchTerm: string = '';

  public loadCustomers(): void {
    this.refresh.emit();
  }

  public onCreateCustomer(): void {
    this.router.navigate(['customers', 'create-customer']);
  }

  public onDeleteClick(): void {
    this.deleteClick.emit();
  }

  public onSearchChange(value: string): void {
    this.searchTerm = value;
    this.searchChanged.emit(value);
  }

  public onSortByField(sortOption: string): void {
    this.sortBy.emit(sortOption);
  }

  public onFilterByField(filterOption: string): void {
    this.filterBy.emit(filterOption);
  }
}
