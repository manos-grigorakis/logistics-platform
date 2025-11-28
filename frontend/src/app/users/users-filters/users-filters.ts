import { Component, EventEmitter, Output, Input, inject } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { SearchBar } from '../../shared/forms/search-bar/search-bar';
import { RoundedIconButton } from '../../shared/forms/rounded-icon-button/rounded-icon-button';
import { DropdownButton } from '../../shared/ui/dropdown-button/dropdown-button';
import { Router } from '@angular/router';

@Component({
  selector: 'app-users-filters',
  imports: [NgIcon, SearchBar, RoundedIconButton, DropdownButton],
  templateUrl: './users-filters.html',
  styleUrl: './users-filters.css',
})
export class UsersFilters {
  @Output() refresh = new EventEmitter<void>();
  @Output() deleteClick = new EventEmitter<void>();
  @Output() searchChanged = new EventEmitter<string>();
  @Input() isLoading: boolean = false;
  @Input() isDeleteDisabled?: boolean;
  private router: Router = inject(Router);

  public searchTerm: string = '';

  loadUsers(): void {
    this.refresh.emit();
  }

  public onCreateUser(): void {
    this.router.navigate(['users', 'create-user']);
  }

  public onDeleteClick(): void {
    this.deleteClick.emit();
  }

  public onSearchChange(value: string): void {
    this.searchTerm = value;
    this.searchChanged.emit(value);
  }
}
