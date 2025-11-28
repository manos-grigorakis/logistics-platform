import { Component, inject } from '@angular/core';
import { RoleForm } from '../role-form/role-form';
import { RolesService } from '../roles.service';
import { Router } from '@angular/router';
import { toast } from 'ngx-sonner';
import { RoleRequest } from '../models/role-request';

@Component({
  selector: 'app-create-role',
  imports: [RoleForm],
  templateUrl: './create-role.html',
  styleUrl: './create-role.css',
})
export class CreateRole {
  private rolesService: RolesService = inject(RolesService);
  private router: Router = inject(Router);

  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  public onSubmit(data: RoleRequest): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.rolesService.createRole(data).subscribe({
      next: (res) => {
        this.isLoading = false;
        toast.success('Role created successfully');
        this.router.navigate(['/roles']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 409) {
          this.errorMessage = 'Role already exists';
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }
}
