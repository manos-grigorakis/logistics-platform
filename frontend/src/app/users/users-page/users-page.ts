import { Component, inject, OnInit } from '@angular/core';
import { UsersTable } from '../users-table/users-table';
import { UsersService } from '../users.service';
import { UserResponse } from '../models/user-response';
import { UsersFilters } from '../users-filters/users-filters';
import { toast } from 'ngx-sonner';
import { forkJoin } from 'rxjs';
import { Modal } from '../../shared/ui/modal/modal';

@Component({
  selector: 'app-users-page',
  imports: [UsersTable, UsersFilters, Modal],
  templateUrl: './users-page.html',
  styleUrl: './users-page.css',
})
export class UsersPage implements OnInit {
  private usersService: UsersService = inject(UsersService);

  public isLoading: boolean = false;
  public users: UserResponse[] = [];
  public displayedUsers: UserResponse[] = [];
  public selectedUserIds = new Set<number>();
  public disableDeleteButton: boolean = true;
  public showModal: boolean = false;
  public modalHeader: string = '';
  public modalMessage: string = '';
  public searchTerm: string = '';

  ngOnInit(): void {
    this.loadUsers();
  }

  public loadUsers(): void {
    this.isLoading = true;
    this.usersService.fetchUsers().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.users = res;
        this.displayedUsers = res;
        this.selectedUserIds.clear();
      },
      error: (err) => {
        this.isLoading = false;
      },
    });
  }

  public toggleUserSelection(userId: number): void {
    if (this.selectedUserIds.has(userId)) {
      this.selectedUserIds.delete(userId);
    } else {
      this.selectedUserIds.add(userId);
      console.log(userId);
    }

    this.disableDeleteButton = this.selectedUserIds.size === 0;
  }

  public onUserDeleteClick(): void {
    this.modalHeader = 'Delete Selected Users';
    this.modalMessage = 'This action is permanent and cannot be undone';
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
        toast.success('User(s) deleted successfully');
        this.selectedUserIds.clear();
        this.disableDeleteButton = true;
        this.loadUsers();
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          toast.error('Server error. Please try again');
        } else {
          toast.error('An error has occured. Please try again');
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
      case 'asc-by-first-name':
        this.displayedUsers = [...this.displayedUsers].sort((a, b) =>
          a.firstName.toLocaleLowerCase().localeCompare(b.firstName.toLocaleLowerCase()),
        );
        break;
      case 'desc-by-first-name':
        this.displayedUsers = [...this.displayedUsers].sort((a, b) =>
          b.firstName.toLocaleLowerCase().localeCompare(a.firstName.toLocaleLowerCase()),
        );
        break;
      case 'asc-by-last-name':
        this.displayedUsers = [...this.displayedUsers].sort((a, b) =>
          a.lastName.toLocaleLowerCase().localeCompare(b.lastName.toLocaleLowerCase()),
        );
        break;
      case 'desc-by-last-name':
        this.displayedUsers = [...this.displayedUsers].sort((a, b) =>
          b.lastName.toLocaleLowerCase().localeCompare(a.lastName.toLocaleLowerCase()),
        );
        break;
    }
  }
}
