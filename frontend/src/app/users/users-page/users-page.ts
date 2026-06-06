import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { UsersTable } from '../users-table/users-table';
import { UsersService } from '../users.service';
import { UserResponse } from '../models/user-response';
import { UsersFilters } from '../users-filters/users-filters';
import { forkJoin, Subscription, take } from 'rxjs';
import { Modal } from '../../shared/ui/modal/modal';
import { AuthService } from '../../core/auth/services/auth.service';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../shared/services/language.service';

@Component({
  selector: 'app-users-page',
  imports: [UsersTable, UsersFilters, Modal, TranslatePipe],
  templateUrl: './users-page.html',
  styleUrl: './users-page.css',
})
export class UsersPage implements OnInit, OnDestroy {
  public isLoading: boolean = false;
  public users: UserResponse[] = [];
  public displayedUsers: UserResponse[] = [];
  public errorMessage?: string;
  public selectedUserIds = new Set<number>();
  public disableDeleteButton: boolean = true;
  public showModal: boolean = false;
  public modalHeader: string = '';
  public modalMessage: string = '';
  public searchTerm: string = '';
  public sortLabel: string = '';
  public currentUserId: number | null = null;

  // Services
  private usersService: UsersService = inject(UsersService);
  private authService: AuthService = inject(AuthService);
  private languageService = inject(LanguageService);

  private langChangeSub?: Subscription;

  ngOnInit(): void {
    this.loadUsers();
    this.currentUserId = this.authService.getUserId();

    this.setLabels();
    this.langChangeSub = this.languageService.onLangChange.subscribe(() => this.setLabels());
  }

  ngOnDestroy(): void {
    this.langChangeSub?.unsubscribe();
  }

  public loadUsers(): void {
    this.isLoading = true;
    this.disableDeleteButton = true;

    this.usersService.fetchUsers().subscribe({
      next: (res) => {
        this.isLoading = false;
        const data = res.data;
        this.users = data;
        this.displayedUsers = data;
        this.selectedUserIds.clear();
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }

  public toggleUserSelection(userId: number): void | null {
    if (this.currentUserId === userId) return null;

    if (this.selectedUserIds.has(userId)) {
      this.selectedUserIds.delete(userId);
    } else {
      this.selectedUserIds.add(userId);
    }

    this.disableDeleteButton = this.selectedUserIds.size === 0;
  }

  public onUserDeleteClick(): void {
    this.modalHeader = 'users.messages.modal.title';
    this.modalMessage = 'users.messages.modal.message';
    this.showModal = true;
  }

  public handleDelete(): void {
    this.deleteUsers();
    this.disableDeleteButton = true;
    this.showModal = false;
  }

  private deleteUsers(): void {
    if (this.selectedUserIds.size === 0) return;
    this.isLoading = true;
    const ids = Array.from(this.selectedUserIds);
    const deleteUsers = ids.map((id) => this.usersService.deleteUser(id));

    forkJoin(deleteUsers).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.languageService.toastSuccess('users.messages.modal.success');
        this.selectedUserIds.clear();
        this.disableDeleteButton = true;
        this.loadUsers();
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          this.languageService.toastError('common.errors.server');
        } else {
          this.languageService.toastError('common.errors.generic');
        }
      },
    });
  }

  public hideModal(): void {
    this.showModal = false;
  }

  public onSearchChanged(value: string): void {
    this.searchTerm = value.trim().toLocaleLowerCase();

    if (!this.searchTerm) {
      this.displayedUsers = this.users;
      return;
    }

    this.displayedUsers = this.users.filter(
      (user) =>
        user.firstName.toLocaleLowerCase().includes(this.searchTerm) ||
        user.lastName.toLocaleLowerCase().includes(this.searchTerm) ||
        user.email.toLocaleLowerCase().includes(this.searchTerm),
    );
  }

  public onSort(sortOption: string): void {
    switch (sortOption) {
      case 'all':
        this.sortLabel = `${this.languageService.translateKey('common.filters.sort-by')}`;
        this.displayedUsers = this.users;
        break;
      case 'asc-by-first-name':
        this.sortLabel = `${this.languageService.translateKey('users.fields.first-name')} (A-Z)`;
        this.displayedUsers = [...this.displayedUsers].sort((a, b) =>
          a.firstName.toLocaleLowerCase().localeCompare(b.firstName.toLocaleLowerCase()),
        );
        break;
      case 'desc-by-first-name':
        this.sortLabel = `${this.languageService.translateKey('users.fields.first-name')} (Z-A)`;
        this.displayedUsers = [...this.displayedUsers].sort((a, b) =>
          b.firstName.toLocaleLowerCase().localeCompare(a.firstName.toLocaleLowerCase()),
        );
        break;
      case 'asc-by-last-name':
        this.sortLabel = `${this.languageService.translateKey('users.fields.last-name')} (A-Z)`;
        this.displayedUsers = [...this.displayedUsers].sort((a, b) =>
          a.lastName.toLocaleLowerCase().localeCompare(b.lastName.toLocaleLowerCase()),
        );
        break;
      case 'desc-by-last-name':
        this.sortLabel = `${this.languageService.translateKey('users.fields.last-name')} (Z-A)`;
        this.displayedUsers = [...this.displayedUsers].sort((a, b) =>
          b.lastName.toLocaleLowerCase().localeCompare(a.lastName.toLocaleLowerCase()),
        );
        break;
    }
  }

  private setLabels(): void {
    this.languageService
      .translateKeyAsync('common.filters.sort-by')
      .pipe(take(1))
      .subscribe((val) => (this.sortLabel = val));
  }
}
