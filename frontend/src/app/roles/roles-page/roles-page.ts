import { Component, inject, OnInit } from '@angular/core';
import { RolesTable } from '../roles-table/roles-table';
import { Role } from '../models/role';
import { RolesService } from '../roles.service';
import { Modal } from '../../shared/ui/modal/modal';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-roles-page',
  imports: [RolesTable, Modal],
  templateUrl: './roles-page.html',
  styleUrl: './roles-page.css',
})
export class RolesPage implements OnInit {
  private rolesService: RolesService = inject(RolesService);
  private selectedIdForDelete: number | null = null;

  public isLoading: boolean = false;
  public roles: Role[] = [];
  public errorMessage: string = '';
  public showModal: boolean = false;
  public modalHeader: string = '';
  public modalMessage: string = '';

  ngOnInit(): void {
    this.fetchRoles();
  }

  private fetchRoles(): void {
    this.isLoading = true;
    this.rolesService.fetchRoles().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.roles = res;
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }

  public onRoleDeleteRequest(id: number): void {
    this.selectedIdForDelete = id;
    this.openDeleteModal();
  }

  public handleDeleteRole(): void {
    if (this.selectedIdForDelete === null) return;

    this.hideModal();
    this.isLoading = true;

    this.rolesService.deleteRole(this.selectedIdForDelete).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.selectedIdForDelete === null;
        toast.success('Role deleted successfully');
        this.fetchRoles();
      },
      error: (err) => {
        this.isLoading = false;
        this.selectedIdForDelete === null;

        if (err.status === 409) {
          toast.error('This role cannot be deleted because it is assigned to existing users.');
        } else if (err.status === 500) {
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

  private openDeleteModal(): void {
    this.modalHeader = 'Delete Selected Role';
    this.modalMessage = 'This action is permanent and cannot be undone';
    this.showModal = true;
  }
}
