import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { MainInput } from '../../shared/forms/main-input/main-input';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { RoleRequest } from '../models/role-request';
import { ReactiveFormsModule, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Role } from '../models/role';

@Component({
  selector: 'app-role-form',
  imports: [ReactiveFormsModule, PrimaryButton, MainInput, LoadingSpinner],
  templateUrl: './role-form.html',
  styleUrl: './role-form.css',
})
export class RoleForm {
  @Input() formUsage: 'create' | 'update' = 'create';
  @Input() isLoading: boolean = false;
  @Input() errorMessage?: string;
  @Output() onFormSubmit = new EventEmitter<RoleRequest>();

  private formBuilder: FormBuilder = inject(FormBuilder);
  private role?: Role;

  roleForm = this.formBuilder.group({
    name: new FormControl<string>('', { nonNullable: true, validators: Validators.required }),
    description: new FormControl<string>(''),
  });

  @Input() set roleData(value: Role | undefined) {
    this.role = value;

    if (value) {
      this.roleForm.patchValue({
        name: value.name,
        description: value.description,
      });
    }
  }

  get name(): FormControl {
    return this.roleForm.get('name') as FormControl;
  }

  get description(): FormControl {
    return this.roleForm.get('description') as FormControl;
  }

  public onSubmit(): void {
    if (this.roleForm.invalid) {
      // Mark all controls as touched & re-run validators
      Object.values(this.roleForm.controls).forEach((control) => {
        control.markAsTouched();
        control.updateValueAndValidity({ emitEvent: true });
      });
      return;
    }

    const { name, description } = this.roleForm.getRawValue();

    const data: RoleRequest = {
      name,
      description: description?.trim() === '' ? null : description,
    };

    this.onFormSubmit.emit(data);
  }
}
