import { Component, inject, OnInit } from '@angular/core';
import { UsersService } from '../users.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UsersListResponse } from '../models/users-list-response';
import { ReactiveFormsModule } from '@angular/forms';
import { CreateUserRequest } from '../models/create-user-request';
import { toast } from 'ngx-sonner';
import { Role } from '../../roles/models/role';
import { UserForm } from '../user-form/user-form';

@Component({
  selector: 'app-edit-user',
  imports: [ReactiveFormsModule, UserForm],
  templateUrl: './edit-user.html',
  styleUrl: './edit-user.css',
})
export class EditUser implements OnInit {
  private userService: UsersService = inject(UsersService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);
  private id: number = 0;
  public user?: UsersListResponse;

  public errorMessage?: string;
  public isLoading: boolean = false;

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) return;

    this.id = parseInt(tempId);
    this.fetchUserData(this.id);
  }

  public onSubmit(data: CreateUserRequest): void {
    this.errorMessage = undefined;
    this.isLoading = true;

    this.userService.updateUser(this.id, data).subscribe({
      next: (res) => {
        this.isLoading = false;
        toast.success('User updated successfully');
        this.router.navigate(['/users']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 409) {
          this.errorMessage = `User already exists with email ${data.email}`;
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }

  private fetchUserData(id: number): void {
    this.userService.getUser(id).subscribe({
      next: (res) => {
        this.user = res;
        this.errorMessage = undefined;
      },
      error: (err) => {
        if (err.status === 404) {
          this.errorMessage = "User doesn't exists";
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error has occured. Please try again';
        }
      },
    });
  }
}
