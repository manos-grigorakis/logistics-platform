import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { FileDropzone } from '../../shared/ui/file-dropzone/file-dropzone';
import { CustomersService } from '../../customers/customers.service';
import { Customer } from '../../customers/models/customer';
import { NgSelectComponent } from '@ng-select/ng-select';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { NgIcon } from '@ng-icons/core';
import { DetailedStepper } from '../../shared/ui/detailed-stepper/detailed-stepper';
import { FormBuilder, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { PaymentsService } from '../payments.service';
import { ReconciliationProcessRequest } from '../models/reconciliaton-process-request';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-payments-page',
  imports: [
    FileDropzone,
    NgSelectComponent,
    PrimaryButton,
    NgIcon,
    DetailedStepper,
    ReactiveFormsModule,
  ],
  templateUrl: './payments-page.html',
  styleUrl: './payments-page.css',
})
export class PaymentsPage implements OnInit {
  // Stepper
  public activeStep: number = 0;
  public stepperData: { title: string; description: string }[] = [
    { title: 'Select Customer & Upload', description: 'Choose a customer and upload files' },
    { title: 'Processing', description: 'Matching Transactions' },
    { title: 'Results', description: 'Download results' },
  ];

  // Customer
  @ViewChild('customerSelect') customerSelect!: any;
  public customersList: Customer[] = [];
  public customersLoading: boolean = false;
  public customersErrorMessage?: string = undefined;
  public customerSearch$: Subject<string> = new Subject<string>();

  private customersService = inject(CustomersService);
  private paymentService = inject(PaymentsService);

  public isFormValid: boolean = false;
  private formBuilder = inject(FormBuilder);

  reconciliationForm = this.formBuilder.group({
    customerId: new FormControl<number | null>(null, Validators.required),
    invoicesFile: new FormControl<File | null>(null, Validators.required),
    bankStatementFile: new FormControl<File | null>(null, Validators.required),
  });

  ngOnInit(): void {
    // Debouncer
    this.customerSearch$.pipe(debounceTime(300), distinctUntilChanged()).subscribe((name) => {
      this.fetchCustomers(name);
    });

    this.fetchCustomers('');
  }

  public onClickFocusCustomersSelect(): void {
    this.customerSelect.focus();
  }

  public onInvoicesFileSelected(file: File): void {
    this.invoicesFile.setValue(file);
  }

  public onBankFileSelected(file: File): void {
    this.bankStatementFile.setValue(file);
  }

  // Getters
  public get customerId(): FormControl {
    return this.reconciliationForm.get('customerId') as FormControl;
  }

  public get invoicesFile(): FormControl {
    return this.reconciliationForm.get('invoicesFile') as FormControl;
  }

  public get bankStatementFile(): FormControl {
    return this.reconciliationForm.get('bankStatementFile') as FormControl;
  }

  public onFormSubmit(): void {
    if (this.reconciliationForm.invalid) {
      this.reconciliationForm.markAllAsTouched();
      return;
    }

    const payload: ReconciliationProcessRequest = {
      customerId: this.customerId.getRawValue(),
      invoiceFile: this.invoicesFile.getRawValue(),
      bankStatement: this.bankStatementFile.getRawValue(),
    };

    this.paymentService.reconciliationProcess(payload).subscribe({
      next: (res) => {
        console.log(res);
      },
      error: (err) => {
        let errorCode = err?.error?.errorCode;

        switch (err.status) {
          case 400:
            toast.error('Failed to process the uploaded files. Please try again');
            break;
          case 404:
            toast.error('Failed to process the uploaded files. Please try again');
            break;
          case 409:
            if (errorCode === 'CUSTOMER_TIN_MISMATCH') {
              toast.error("The selected customer's TIN number doesn't match the invoices file");
            }
            break;
          case 500:
            toast.error('Server error. Please try again');
            break;
          default:
            toast.error('An error occurred. Please try again');
        }
      },
    });
  }

  private fetchCustomers(customer: string): void {
    this.customersLoading = true;
    this.customersErrorMessage = undefined;

    this.customersService.fetchCustomers({ companyName: customer }).subscribe({
      next: (res) => {
        this.customersLoading = false;
        this.customersList = res.content;
      },
      error: (err) => {
        this.customersLoading = false;
        this.customersErrorMessage = 'Failed to fetch Customers';
      },
    });
  }
}
