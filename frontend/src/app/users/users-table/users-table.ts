import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { UsersListResponse } from '../models/users-list-response';

@Component({
  selector: 'app-users-table',
  imports: [RouterLink, LoadingSpinner],
  templateUrl: './users-table.html',
  styleUrl: './users-table.css',
})
export class UsersTable {
  @Input() isLoading?: boolean;
  @Input() users: UsersListResponse[] = [];
}
