import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { CustomersService } from '../../customers/customers.service';
import { Customer } from '../../customers/models/customer';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { DetailedStepper } from '../../shared/ui/detailed-stepper/detailed-stepper';
import { FormBuilder, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { PaymentsService } from '../payments.service';
import { ReconciliationProcessRequest } from '../models/reconciliaton-process-request';
import { toast } from 'ngx-sonner';
import { ReconciliationProcessResponse } from '../models/reconciliation-process-response';
import { ReconciliationForm } from './reconciliation-form/reconciliation-form';
import { ReconciliationResultsTab } from './reconciliation-results-tab/reconciliation-results-tab';
import { ReconciliationProcessTab } from './reconciliation-process-tab/reconciliation-process-tab';

@Component({
  selector: 'app-payments-page',
  imports: [
    DetailedStepper,
    ReactiveFormsModule,
    ReconciliationForm,
    ReconciliationProcessTab,
    ReconciliationResultsTab,
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

  public results?: ReconciliationProcessResponse;

  // Customer
  public customersList: Customer[] = [];
  public customersLoading: boolean = false;
  public customersErrorMessage?: string = undefined;
  public customerSearch$: Subject<string> = new Subject<string>();

  // Services
  private customersService = inject(CustomersService);
  private paymentService = inject(PaymentsService);

  // Form
  public isFormValid: boolean = false;
  private formBuilder = inject(FormBuilder);

  reconciliationForm = this.formBuilder.group({
    customerId: new FormControl<number | null>(null, Validators.required),
    invoiceFile: new FormControl<File | null>(null, Validators.required),
    bankStatementFile: new FormControl<File | null>(null, Validators.required),
  });

  ngOnInit(): void {
    // Debouncer
    this.customerSearch$.pipe(debounceTime(300), distinctUntilChanged()).subscribe((name) => {
      this.fetchCustomers(name);
    });

    this.fetchCustomers('');
  }

  public onFormSubmit(): void {
    if (this.reconciliationForm.invalid) {
      this.reconciliationForm.markAllAsTouched();
      return;
    }

    // Processing step
    this.activeStep = 1;

    const { customerId, invoiceFile, bankStatementFile } = this.reconciliationForm.getRawValue();
    const payload: ReconciliationProcessRequest = {
      customerId: customerId!,
      invoiceFile: invoiceFile!,
      bankStatementFile: bankStatementFile!,
    };

    const responseStartTime = performance.now();

    this.paymentService.reconciliationProcess(payload).subscribe({
      next: (res) => {
        const responseDuration = performance.now() - responseStartTime;
        const minTime = 900;
        const delay = Math.max(minTime - responseDuration, 0);

        // Timeout to show the UI processing state
        setTimeout(() => {
          // Results step
          this.activeStep = 2;
          this.results = res;
        }, delay);
      },
      error: (err) => {
        this.activeStep = 0;

        let errorCode = err?.error?.errorCode;

        switch (err.status) {
          case 400:
            if (errorCode === 'NO_INVOICES_FOUND') {
              toast.error('No invoices were found in the uploaded file');
            } else {
              toast.error('Failed to process the uploaded files. Please try again');
            }
            break;
          case 404:
            toast.error('Failed to process the uploaded files. Please try again');
            break;
          case 409:
            if (errorCode === 'CUSTOMER_TIN_MISMATCH') {
              toast.error("The selected customer's TIN number doesn't match the invoices file");
            } else if (errorCode === 'DUPLICATE_REPORT_NAME') {
              toast.error('A report already exists for this invoice range');
            } else if (errorCode === 'INVOICES_ALREADY_EXIST') {
              toast.error('All invoices already exist in the system');
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

  public onNewReconciliation(): void {
    this.activeStep = 0;
    this.reconciliationForm.reset();
    this.results = undefined;
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
