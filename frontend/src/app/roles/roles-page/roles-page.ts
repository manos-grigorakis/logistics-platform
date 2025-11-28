import { Component, inject, OnInit } from '@angular/core';
import { RolesTable } from '../roles-table/roles-table';
import { Role } from '../models/role';
import { RolesService } from '../roles.service';

@Component({
  selector: 'app-roles-page',
  imports: [RolesTable],
  templateUrl: './roles-page.html',
  styleUrl: './roles-page.css',
})
export class RolesPage implements OnInit {
  private rolesService: RolesService = inject(RolesService);

  public isLoading: boolean = false;
  public roles: Role[] = [];
  public errorMessage: string = '';

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
}
