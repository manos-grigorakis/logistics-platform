import { Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CustomersService } from '../../customers/customers.service';
import { Customer } from '../../customers/models/customer';
import { debounceTime, distinctUntilChanged, Subject, Subscription, take } from 'rxjs';
import { DetailedStepper } from '../../shared/ui/detailed-stepper/detailed-stepper';
import { FormBuilder, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { PaymentsService } from '../payments.service';
import { ReconciliationProcessRequest } from '../models/reconciliaton-process-request';
import { toast } from 'ngx-sonner';
import { ReconciliationProcessResponse } from '../models/reconciliation-process-response';
import { ReconciliationForm } from './reconciliation-form/reconciliation-form';
import { ReconciliationResultsTab } from './reconciliation-results-tab/reconciliation-results-tab';
import { ReconciliationProcessTab } from './reconciliation-process-tab/reconciliation-process-tab';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../shared/services/language.service';

@Component({
  selector: 'app-payments-page',
  imports: [
    DetailedStepper,
    ReactiveFormsModule,
    ReconciliationForm,
    ReconciliationProcessTab,
    ReconciliationResultsTab,
    TranslatePipe,
  ],
  templateUrl: './payments-page.html',
  styleUrl: './payments-page.css',
})
export class PaymentsPage implements OnInit, OnDestroy {
  // Stepper
  public activeStep: number = 0;
  public stepperData: { title: string; description: string }[] = [];

  public results?: ReconciliationProcessResponse;

  // Customer
  public customersList: Customer[] = [];
  public customersLoading: boolean = false;
  public customersErrorMessage?: string = undefined;
  public customerSearch$: Subject<string> = new Subject<string>();

  // Services
  private customersService = inject(CustomersService);
  private paymentService = inject(PaymentsService);
  private languageService = inject(LanguageService);

  // Form
  public isFormValid: boolean = false;
  private formBuilder = inject(FormBuilder);

  reconciliationForm = this.formBuilder.group({
    customerId: new FormControl<number | null>(null, Validators.required),
    invoiceFile: new FormControl<File | null>(null, Validators.required),
    bankStatementFile: new FormControl<File | null>(null, Validators.required),
  });

  private langChangeSub?: Subscription;

  ngOnInit(): void {
    // Debouncer
    this.customerSearch$.pipe(debounceTime(300), distinctUntilChanged()).subscribe((name) => {
      this.fetchCustomers(name);
    });

    this.setStepperValues();
    this.langChangeSub = this.languageService.onLangChange.subscribe(() => this.setStepperValues());
    this.fetchCustomers('');
  }

  ngOnDestroy(): void {
    this.langChangeSub?.unsubscribe();
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
          this.results = res.data;
        }, delay);
      },
      error: (err) => {
        this.activeStep = 0;

        let errorCode = err?.error?.error?.errorCode;

        switch (err.status) {
          case 400:
            if (errorCode === 'NO_INVOICES_FOUND') {
              this.languageService.toastError('payments.errors.no-invoices-found-in-file');
            } else {
              this.languageService.toastError('common.errors.files-processing');
            }
            break;
          case 404:
            this.languageService.toastError('common.errors.files-processing');
            break;
          case 409:
            if (errorCode === 'CUSTOMER_TIN_MISMATCH') {
              this.languageService.toastError('payments.errors.tin-mismatch');
            } else if (errorCode === 'DUPLICATE_REPORT_NAME') {
              this.languageService.toastError('payments.errors.duplicate-report');
            } else if (errorCode === 'INVOICES_ALREADY_EXIST') {
              this.languageService.toastError('payments.errors.already-exists-in-system');
            }
            break;
          case 500:
            this.languageService.toastError('common.errors.server');
            break;
          default:
            this.languageService.toastError('common.errors.generic');
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
        this.customersList = res.data.content;
      },
      error: () => {
        this.customersLoading = false;
        this.customersErrorMessage = 'payments.errors.fetch-customers';
      },
    });
  }

  /**
   * Sets the stepper values using keys from translation
   */
  private setStepperValues(): void {
    const keys = [
      'payments.stepper.one.title',
      'payments.stepper.one.description',
      'payments.stepper.two.title',
      'payments.stepper.two.description',
      'payments.stepper.three.title',
      'payments.stepper.three.description',
    ];

    this.languageService
      .translateKeyAsync(keys)
      .pipe(take(1))
      .subscribe((translations: any) => {
        this.stepperData = [
          { title: translations[keys[0]], description: translations[keys[1]] },
          { title: translations[keys[2]], description: translations[keys[3]] },
          { title: translations[keys[4]], description: translations[keys[5]] },
        ];
      });
  }
}
