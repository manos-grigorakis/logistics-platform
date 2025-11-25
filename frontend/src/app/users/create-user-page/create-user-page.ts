import { Component, inject } from '@angular/core';
import { UsersService } from '../users.service';
import { CreateUserRequest } from '../models/create-user-request';
import { toast } from 'ngx-sonner';
import { Router } from '@angular/router';
import { UserForm } from '../user-form/user-form';

@Component({
  selector: 'app-create-user-page',
  imports: [UserForm],
  templateUrl: './create-user-page.html',
  styleUrl: './create-user-page.css',
})
export class CreateUserPage {
  private userService: UsersService = inject(UsersService);
  private router = inject(Router);
  public isLoading: boolean = false;
  public errorMessage?: string;

  public onSubmit(data: CreateUserRequest): void {
    this.errorMessage = undefined;
    this.isLoading = true;

    this.userService.createUser(data).subscribe({
      next: (res) => {
        this.isLoading = false;
        toast.success('User created successfully');
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
}
