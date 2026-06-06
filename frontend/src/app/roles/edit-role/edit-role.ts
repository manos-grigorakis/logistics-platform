import { Component, inject, OnInit } from '@angular/core';
import { RolesService } from '../roles.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from '../models/role';
import { RoleRequest } from '../models/role-request';
import { RoleForm } from '../role-form/role-form';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../core/services/language.service';

@Component({
  selector: 'app-edit-role',
  imports: [RoleForm, TranslatePipe],
  templateUrl: './edit-role.html',
  styleUrl: './edit-role.css',
})
export class EditRole implements OnInit {
  private rolesService: RolesService = inject(RolesService);
  private languageService = inject(LanguageService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);
  private id: number = 0;

  public isLoading: boolean = false;
  public errorMessage?: string;
  public role?: Role;

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) {
      this.languageService.toastError('roles.messages.invalid-role-id');
      this.router.navigate(['/roles']);
      return;
    }

    this.id = parseInt(tempId);
    this.fetchRoleById(this.id);
  }

  public onSubmit(data: RoleRequest): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.rolesService.updateRole(this.id, data).subscribe({
      next: () => {
        this.isLoading = false;
        this.languageService.toastSuccess('roles.messages.success-update');
        this.router.navigate(['/roles']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 403) {
          this.errorMessage = 'roles.messages.protected-role';
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }

  private fetchRoleById(id: number): void {
    this.isLoading = true;

    this.rolesService.fetchRole(id).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.errorMessage = undefined;

        if (!res.data.editable) {
          this.languageService.toastWarning('roles.messages.protected-role');
          this.router.navigate(['/roles']);
          return;
        }

        this.role = res.data;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.errorMessage = 'roles.messages.not-found';
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }
}
