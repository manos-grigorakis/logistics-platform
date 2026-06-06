import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { DropdownButton } from '../../ui/dropdown-button/dropdown-button';
import { NgIcon } from '@ng-icons/core';
import { RoundedIconButton } from '../forms/rounded-icon-button/rounded-icon-button';
import { SearchBar } from '../forms/search-bar/search-bar';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-quotes-filters',
  imports: [DropdownButton, NgIcon, RoundedIconButton, SearchBar, TranslatePipe],
  templateUrl: './quotes-filters.html',
  styleUrl: './quotes-filters.css',
})
export class QuotesFilters {
  @Output() refresh = new EventEmitter<void>();
  @Output() searchChanged = new EventEmitter<string>();
  @Output() sortBy = new EventEmitter<string>();
  @Output() filterBy = new EventEmitter<string>();

  @Input() isLoading: boolean = false;
  @Input() sortLabel?: string;
  @Input() filterLabel?: string;
  @Input() searchPlaceholder: string = '';
  @Input() createButton: boolean = true;

  public searchTerm: string = '';

  private router: Router = inject(Router);

  public onSearchChange(value: string): void {
    this.searchTerm = value;
    this.searchChanged.emit(value);
  }

  public onCreateQuoteClick(): void {
    this.router.navigate(['quotes', 'create-quote']);
  }

  public onLoadQuotesClick(): void {
    this.refresh.emit();
  }

  public onSortByFieldClick(sortOption: string) {
    this.sortBy.emit(sortOption);
  }

  public onFilterByFieldClick(filterOption: string) {
    this.filterBy.emit(filterOption);
  }
}
