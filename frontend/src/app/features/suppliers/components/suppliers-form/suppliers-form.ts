import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { ErrorAlert } from '../../../../shared/ui/error-alert/error-alert';
import { MainInput } from '../../../../shared/components/forms/main-input/main-input';
import { PrimaryButton } from '../../../../shared/ui/primary-button/primary-button';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Supplier } from '../../models/supplier.interface';
import { SupplierRequest } from '../../models/supplier-request.interface';

@Component({
  selector: 'app-suppliers-form',
  imports: [
    TranslatePipe,
    LoadingSpinner,
    ErrorAlert,
    ReactiveFormsModule,
    MainInput,
    PrimaryButton,
  ],
  templateUrl: './suppliers-form.html',
  styleUrl: './suppliers-form.css',
})
export class SuppliersForm {
  @Input() formUsage: 'create' | 'update' = 'create';
  @Input({ required: true }) isLoading!: boolean;
  @Input() errorMessage?: string;
  @Input() supplier?: Supplier;

  @Output() onSubmit = new EventEmitter<SupplierRequest>();

  private formBuilder = inject(FormBuilder);

  form = this.formBuilder.group({
    companyName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(100)],
    }),
    email: new FormControl<string>('', [Validators.email, Validators.maxLength(320)]),
  });

  @Input() set supplierData(value: Supplier | undefined) {
    if (value) {
      this.form.patchValue({ companyName: value.companyName, email: value.email });
    }
  }

  get companyName(): FormControl {
    return this.form.get('companyName') as FormControl;
  }

  get email(): FormControl {
    return this.form.get('email') as FormControl;
  }

  public onSubmitClick(): void {
    if (this.form.invalid) {
      // Mark all controls as touched & re-run validators
      Object.values(this.form.controls).forEach((control) => {
        control.markAsTouched();
        control.updateValueAndValidity({ emitEvent: true });
      });
      return;
    }

    const data: SupplierRequest = {
      companyName: this.companyName.getRawValue(),
      email: this.email.getRawValue() === '' ? null : this.email.getRawValue(),
    };

    this.onSubmit.emit(data);
  }
}
