import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { MainInput } from '../../shared/forms/main-input/main-input';
import { ErrorAlert } from '../../shared/ui/error-alert/error-alert';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { FormBuilder, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { VehicleRequest } from '../models/vehicle-request';
import { Vehicle } from '../models/vehicle';
import { VehicleResponse } from '../models/vehicle-response';

@Component({
  selector: 'app-vehicle-form',
  imports: [PrimaryButton, MainInput, ErrorAlert, LoadingSpinner, ReactiveFormsModule],
  templateUrl: './vehicle-form.html',
  styleUrl: './vehicle-form.css',
})
export class VehicleForm {
  @Input() formUsage: 'create' | 'update' = 'create';
  @Input() isLoading!: boolean;
  @Input() errorMessage?: string;
  @Output() onSubmit = new EventEmitter<VehicleRequest>();

  private formBuilder = inject(FormBuilder);

  @Input() set vehicleData(value: VehicleResponse | undefined) {
    if (value) {
      this.vehicleForm.patchValue({
        brand: value.brand,
        plate: value.plate,
        type: value.type,
      });
    }
  }

  vehicleForm = this.formBuilder.group({
    brand: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    plate: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^.{8}$/)],
    }),
    type: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  public get brand(): FormControl {
    return this.vehicleForm.get('brand') as FormControl;
  }

  public get plate(): FormControl {
    return this.vehicleForm.get('plate') as FormControl;
  }

  public get type(): FormControl {
    return this.vehicleForm.get('type') as FormControl;
  }

  public onSubmitClick(): void {
    if (this.vehicleForm.invalid) {
      // Mark all controls as touched & re-run validators
      Object.values(this.vehicleForm.controls).forEach((control) => {
        control.markAsTouched();
        control.updateValueAndValidity({ emitEvent: true });
      });
      return;
    }

    const { brand, plate, type } = this.vehicleForm.getRawValue();

    const payload: VehicleRequest = {
      brand,
      plate,
      type,
    };

    this.onSubmit.emit(payload);
  }
}
