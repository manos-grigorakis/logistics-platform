import { Component, inject, OnInit } from '@angular/core';
import { RolesService } from '../../roles/roles.service';
import { Role } from '../../roles/models/role';
import { FormBuilder, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { UsersService } from '../users.service';
import { CreateUserRequest } from '../models/create-user-request';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { MainInput } from '../../shared/forms/main-input/main-input';
import { toast } from 'ngx-sonner';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-user-page',
  imports: [ReactiveFormsModule, LoadingSpinner, PrimaryButton, MainInput],
  templateUrl: './create-user-page.html',
  styleUrl: './create-user-page.css',
})
export class CreateUserPage implements OnInit {
  private rolesService: RolesService = inject(RolesService);
  private userService: UsersService = inject(UsersService);
  private formBuilder: FormBuilder = inject(FormBuilder);
  private router = inject(Router);
  public roles: Role[] = [];
  public isLoading: boolean = false;
  public errorMessage?: string;

  userForm = this.formBuilder.group({
    firstName: new FormControl<string>('', {
      nonNullable: true,
      validators: Validators.required,
    }),
    lastName: new FormControl('', { nonNullable: true, validators: Validators.required }),
    email: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    phone: new FormControl<string>(''),
    roleId: new FormControl<number | null>(null, {
      validators: Validators.required,
    }),
  });

  ngOnInit(): void {
    this.rolesService.fetchRoles().subscribe({
      next: (res) => {
        this.roles = res;
      },
      error: (err) => {
        if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }

  get email(): FormControl {
    return this.userForm.get('email') as FormControl;
  }

  get roleId(): FormControl {
    return this.userForm.get('roleId') as FormControl;
  }

  public onSubmit(): void {
    if (this.userForm.invalid) {
      // Mark all controls as touched & re-run validators
      Object.values(this.userForm.controls).forEach((control) => {
        control.markAsTouched();
        control.updateValueAndValidity({ emitEvent: true });
      });
      return;
    }

    this.isLoading = true;

    const { firstName, lastName, email, phone, roleId } = this.userForm.getRawValue();

    const data: CreateUserRequest = {
      firstName: firstName,
      lastName: lastName,
      email: email,
      phone: phone?.trim() === '' ? null : phone,
      roleId: roleId!,
    };

    this.userService.createUser(data).subscribe({
      next: (res) => {
        this.isLoading = false;
        toast.success('User created successfully');
        this.router.navigate(['/users']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 409) {
          this.email.reset();
          this.errorMessage = `User already exists with email ${email}`;
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again later';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }
}
