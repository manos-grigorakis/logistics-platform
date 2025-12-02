import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { MainInput } from '../../shared/forms/main-input/main-input';
import { FormBuilder, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { CustomerRequest } from '../models/customer-request';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { MetadataService } from '../../metadata/metadata.service';
import { Customer } from '../models/customer';

@Component({
  selector: 'app-customers-form',
  imports: [LoadingSpinner, MainInput, ReactiveFormsModule, PrimaryButton],
  templateUrl: './customers-form.html',
  styleUrl: './customers-form.css',
})
export class CustomersForm implements OnInit {
  @Input() isLoading: boolean = false;
  @Input() formUsage: 'create' | 'update' = 'create';
  @Input() errorMessage?: string;
  @Output() onFormSubmit = new EventEmitter<CustomerRequest>();

  public loadingCustomerType: boolean = false;
  public customerTypesError?: string = undefined;
  public customerTypes: string[] = [];

  private formBuilder: FormBuilder = inject(FormBuilder);
  private metadataService: MetadataService = inject(MetadataService);
  private customer?: Customer;

  customerForm = this.formBuilder.group({
    companyName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(80)],
    }),
    customerType: new FormControl<string | null>(null, Validators.required),
    tin: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^\d{9}$/)],
    }),
    firstName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(80)],
    }),
    lastName: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(80)],
    }),
    email: new FormControl<string>('', [Validators.email, Validators.maxLength(320)]),
    location: new FormControl<string>('', Validators.maxLength(255)),
    phone: new FormControl<string>('', Validators.maxLength(30)),
  });

  @Input() set customerData(value: Customer | undefined) {
    this.customer = value;

    if (value) {
      this.customerForm.patchValue({
        companyName: value.companyName,
        customerType: value.customerType,
        tin: value.tin,
        firstName: value.firstName,
        lastName: value.lastName,
        email: value.email,
        phone: value.phone,
        location: value.location,
      });
    }
  }

  ngOnInit(): void {
    if (this.formUsage === 'update') {
      this.tin.disable();
    }

    this.fetchCustomerTypes();
  }

  get companyName(): FormControl {
    return this.customerForm.get('companyName') as FormControl;
  }

  get customerType(): FormControl {
    return this.customerForm.get('customerType') as FormControl;
  }

  get tin(): FormControl {
    return this.customerForm.get('tin') as FormControl;
  }

  get firstName(): FormControl {
    return this.customerForm.get('firstName') as FormControl;
  }

  get lastName(): FormControl {
    return this.customerForm.get('lastName') as FormControl;
  }

  get email(): FormControl {
    return this.customerForm.get('email') as FormControl;
  }

  get location(): FormControl {
    return this.customerForm.get('location') as FormControl;
  }

  get phone(): FormControl {
    return this.customerForm.get('phone') as FormControl;
  }

  public onSubmit(): void {
    if (this.customerForm.invalid) {
      // Mark all controls as touched & re-run validators
      Object.values(this.customerForm.controls).forEach((control) => {
        control.markAsTouched();
        control.updateValueAndValidity({ emitEvent: true });
      });
      return;
    }

    const { companyName, tin, customerType, firstName, lastName, email, phone, location } =
      this.customerForm.getRawValue();

    if (!customerType) {
      return;
    }

    const data: CustomerRequest = {
      companyName: companyName.trim(),
      tin,
      customerType,
      firstName,
      lastName,
      email,
      phone: phone?.trim() === '' ? null : phone?.trim(),
      location: location?.trim() === '' ? null : location?.trim(),
    };

    this.onFormSubmit.emit(data);
  }

  private fetchCustomerTypes(): void {
    this.loadingCustomerType = true;
    this.customerTypesError = undefined;

    this.metadataService.fetchCustomersTypes().subscribe({
      next: (res) => {
        this.loadingCustomerType = false;
        this.customerTypesError = undefined;
        this.customerTypes = res;
      },
      error: (err) => {
        this.loadingCustomerType = false;

        if (err.status === 500) {
          this.customerTypesError = 'Server error. Please try again';
        } else {
          this.customerTypesError = 'An error occured. Please try again';
        }
      },
    });
  }
}
