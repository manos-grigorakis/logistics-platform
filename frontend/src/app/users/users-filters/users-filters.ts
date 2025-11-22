import { Component, EventEmitter, Output, Input, HostListener } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { SearchBar } from '../../shared/forms/search-bar/search-bar';
import { RoundedIconButton } from '../../shared/forms/rounded-icon-button/rounded-icon-button';
import { DropdownButton } from '../../shared/ui/dropdown-button/dropdown-button';

@Component({
  selector: 'app-users-filters',
  imports: [NgIcon, SearchBar, RoundedIconButton, DropdownButton],
  templateUrl: './users-filters.html',
  styleUrl: './users-filters.css',
})
export class UsersFilters {
  @Output() refresh = new EventEmitter<void>();
  @Input() isLoading: boolean = false;

  loadUsers(): void {
    this.refresh.emit();
  }
}
