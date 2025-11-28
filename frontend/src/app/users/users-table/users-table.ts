import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { UserResponse } from '../models/user-response';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-users-table',
  imports: [RouterLink, LoadingSpinner, NgClass],
  templateUrl: './users-table.html',
  styleUrl: './users-table.css',
})
export class UsersTable {
  @Input() isLoading?: boolean;
  @Input() users: UserResponse[] = [];
  @Input() selectedUserIds = new Set<number>();
  @Output() toggleUserSelection = new EventEmitter<number>();

  public onRowClick(userId: number): void {
    this.toggleUserSelection.emit(userId);
  }

  public onCheckboxClick(event: MouseEvent, userId: number): void {
    event.stopPropagation();
    this.toggleUserSelection.emit(userId);
  }
}
