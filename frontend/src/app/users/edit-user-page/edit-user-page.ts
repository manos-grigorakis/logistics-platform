import { Component, inject, OnInit } from '@angular/core';
import { UsersService } from '../users.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserResponse } from '../models/user-response';
import { UserRequest } from '../models/user-request';
import { toast } from 'ngx-sonner';
import { UserForm } from '../user-form/user-form';

@Component({
  selector: 'app-edit-user',
  imports: [UserForm],
  templateUrl: './edit-user-page.html',
  styleUrl: './edit-user-page.css',
})
export class EditUserPage implements OnInit {
  private userService: UsersService = inject(UsersService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);
  private id: number = 0;
  public user?: UserResponse;

  public errorMessage?: string;
  public isLoading: boolean = false;

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) return;

    this.id = parseInt(tempId);
    this.fetchUserData(this.id);
  }

  public onSubmit(data: UserRequest): void {
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
