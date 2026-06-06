import { Component, inject, OnInit } from '@angular/core';
import { RolesTable } from '../../components/roles-table/roles-table';
import { Role } from '../../models/role';
import { RolesService } from '../../roles.service';
import { Modal } from '../../../../shared/ui/modal/modal';
import { RouterLink } from '@angular/router';
import { PrimaryButton } from '../../../../shared/ui/primary-button/primary-button';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../../../core/services/language.service';

@Component({
  selector: 'app-roles-page',
  imports: [RolesTable, Modal, RouterLink, PrimaryButton, TranslatePipe],
  templateUrl: './roles-page.html',
  styleUrl: './roles-page.css',
})
export class RolesPage implements OnInit {
  // Services
  private rolesService: RolesService = inject(RolesService);
  private languageService = inject(LanguageService);

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
        this.roles = res.data;
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

  public onRoleDeleteRequest(id: number): void {
    this.selectedIdForDelete = id;
    this.openDeleteModal();
  }

  public handleDeleteRole(): void {
    if (this.selectedIdForDelete === null) return;

    this.hideModal();
    this.isLoading = true;

    this.rolesService.deleteRole(this.selectedIdForDelete).subscribe({
      next: () => {
        this.isLoading = false;
        this.selectedIdForDelete = null;
        this.languageService.toastSuccess('roles.messages.success-deletion');
        this.fetchRoles();
      },
      error: (err) => {
        this.isLoading = false;
        this.selectedIdForDelete = null;

        if (err.status === 409) {
          this.languageService.toastWarning('roles.messages.assigned-users');
        } else if (err.status === 500) {
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

  private openDeleteModal(): void {
    this.modalHeader = 'roles.messages.modal.title';
    this.modalMessage = 'roles.messages.modal.message';
    this.showModal = true;
  }
}
