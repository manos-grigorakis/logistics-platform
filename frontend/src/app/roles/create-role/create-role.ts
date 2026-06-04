import { Component, inject } from '@angular/core';
import { RoleForm } from '../role-form/role-form';
import { RolesService } from '../roles.service';
import { Router } from '@angular/router';
import { toast } from 'ngx-sonner';
import { RoleRequest } from '../models/role-request';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../shared/services/language.service';

@Component({
  selector: 'app-create-role',
  imports: [RoleForm, TranslatePipe],
  templateUrl: './create-role.html',
  styleUrl: './create-role.css',
})
export class CreateRole {
  private rolesService: RolesService = inject(RolesService);
  private languageService = inject(LanguageService);
  private router: Router = inject(Router);

  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  public onSubmit(data: RoleRequest): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.rolesService.createRole(data).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.languageService.toastSuccess('roles.messages.success-creation');
        this.router.navigate(['/roles']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 409) {
          this.errorMessage = 'roles.messages.role-exists';
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }
}
