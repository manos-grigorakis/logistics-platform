import { Component, inject, OnInit, Output } from '@angular/core';
import { UsersTable } from '../users-table/users-table';
import { UsersService } from '../users.service';
import { UsersListResponse } from '../models/users-list-response';
import { UsersFilters } from '../users-filters/users-filters';

@Component({
  selector: 'app-users-page',
  imports: [UsersTable, UsersFilters],
  templateUrl: './users-page.html',
  styleUrl: './users-page.css',
})
export class UsersPage implements OnInit {
  private usersService: UsersService = inject(UsersService);

  public isLoading: boolean = false;
  public users: UsersListResponse[] = [];
  public selectedUserIds = new Set<number>();

  ngOnInit(): void {
    this.loadUsers();
  }

  public loadUsers(): void {
    this.isLoading = true;
    this.usersService.fetchUsers().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.users = res;
        this.selectedUserIds.clear();
      },
      error: (err) => {
        this.isLoading = false;
        console.log(err);
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
  }
}
