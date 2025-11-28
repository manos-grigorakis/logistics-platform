import { Component, inject, OnInit } from '@angular/core';
import { RolesService } from '../roles.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from '../models/role';
import { RoleRequest } from '../models/role-request';
import { toast } from 'ngx-sonner';
import { RoleForm } from '../role-form/role-form';

@Component({
  selector: 'app-edit-role',
  imports: [RoleForm],
  templateUrl: './edit-role.html',
  styleUrl: './edit-role.css',
})
export class EditRole implements OnInit {
  private rolesService: RolesService = inject(RolesService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);
  private id: number = 0;

  public isLoading: boolean = false;
  public errorMessage?: string;
  public role?: Role;

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) {
      toast.error('Invalid role id');
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
      next: (res) => {
        this.isLoading = false;
        this.router.navigate(['/roles']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 403) {
          this.errorMessage = 'Role is protected and cannot be edited';
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
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

        if (!res.editable) {
          toast.warning('This role is protected');
          this.router.navigate(['/roles']);
          return;
        }

        this.role = res;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.errorMessage = 'Role not found';
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error has occured. Please try again';
        }
      },
    });
  }
}
