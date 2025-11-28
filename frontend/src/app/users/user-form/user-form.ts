import { Component, EventEmitter, inject, OnInit, Input, Output } from '@angular/core';
import { FormBuilder, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { MainInput } from '../../shared/forms/main-input/main-input';
import { RolesService } from '../../roles/roles.service';
import { Role } from '../../roles/models/role';
import { UserResponse } from '../models/user-response';
import { UserRequest } from '../models/user-request';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule, PrimaryButton, LoadingSpinner, MainInput],
  templateUrl: './user-form.html',
  styleUrl: './user-form.css',
})
export class UserForm implements OnInit {
  @Input() isLoading: boolean = false;
  @Input() formUsage: 'create' | 'update' = 'create';
  @Input() errorMessage?: string;
  @Output() onFormSubmit = new EventEmitter<UserRequest>();

  private rolesService: RolesService = inject(RolesService);
  private formBuilder: FormBuilder = inject(FormBuilder);
  private user?: UserResponse;

  public roles: Role[] = [];
  public roleErrorMessage?: string;
  public roleIsLoading: boolean = false;

  userForm = this.formBuilder.group({
    firstName: new FormControl<string>('', {
      nonNullable: true,
      validators: Validators.required,
    }),
    lastName: new FormControl<string>('', {
      nonNullable: true,
      validators: Validators.required,
    }),
    email: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    phone: new FormControl<string>(''),
    roleId: new FormControl<number | null>(null, {
      validators: Validators.required,
    }),
  });

  @Input() set userData(value: UserResponse | undefined) {
    this.user = value;

    if (value && this.formUsage === 'update') {
      this.userForm.patchValue({
        firstName: value.firstName,
        lastName: value.lastName,
        email: value.email,
        phone: value.phone,
        roleId: value.roleId,
      });
    }
  }

  ngOnInit(): void {
    this.fetchRoles();
  }

  get firstName(): FormControl {
    return this.userForm.get('firstName') as FormControl;
  }

  get lastName(): FormControl {
    return this.userForm.get('lastName') as FormControl;
  }

  get email(): FormControl {
    return this.userForm.get('email') as FormControl;
  }

  get phone(): FormControl {
    return this.userForm.get('phone') as FormControl;
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

    const { firstName, lastName, email, phone, roleId } = this.userForm.getRawValue();

    const data: UserRequest = {
      firstName,
      lastName,
      email,
      phone: phone?.trim() === '' ? null : phone,
      roleId: roleId!,
    };

    this.onFormSubmit.emit(data);
  }

  private fetchRoles(): void {
    this.roleIsLoading = true;
    this.rolesService.fetchRoles().subscribe({
      next: (res) => {
        this.roleIsLoading = false;
        this.roles = res;
      },
      error: (err) => {
        this.roleIsLoading = false;

        if (err.status === 500) {
          this.roleErrorMessage = 'Server error. Please try again later';
        } else {
          this.roleErrorMessage = 'An error occured. Please try again';
        }
      },
    });
  }
}
