import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { DropdownButton } from '../../shared/ui/dropdown-button/dropdown-button';
import { NgIcon } from '@ng-icons/core';
import { RoundedIconButton } from '../../shared/forms/rounded-icon-button/rounded-icon-button';
import { SearchBar } from '../../shared/forms/search-bar/search-bar';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../shared/services/language.service';
import { take } from 'rxjs';

@Component({
  selector: 'app-quotes-filters',
  imports: [DropdownButton, NgIcon, RoundedIconButton, SearchBar, TranslatePipe],
  templateUrl: './quotes-filters.html',
  styleUrl: './quotes-filters.css',
})
export class QuotesFilters implements OnInit {
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
  private languangeService = inject(LanguageService);

  ngOnInit(): void {
    if (!this.searchPlaceholder) {
      this.languangeService
        .translateKeyAsync('quotes.filters.search-by-number-or-company-name')
        .pipe(take(1))
        .subscribe((val) => (this.searchPlaceholder = val));
    }
  }

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
