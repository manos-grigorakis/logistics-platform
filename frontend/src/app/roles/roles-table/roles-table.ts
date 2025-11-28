import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Role } from '../models/role';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';

@Component({
  selector: 'app-roles-table',
  imports: [RouterLink, LoadingSpinner],
  templateUrl: './roles-table.html',
  styleUrl: './roles-table.css',
})
export class RolesTable {
  @Input() isLoading?: boolean;
  @Input() roles?: Role[];
  @Output() deleteRole = new EventEmitter<number>();

  public onDeleteRole(id: number): void {
    this.deleteRole.emit(id);
  }
}
