import { AfterViewInit, Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { PrimaryButton } from '../../../../shared/ui/primary-button/primary-button';
import { MetadataService } from '../../../../core/metadata/metadata.service';
import { CustomersService } from '../../../customers/customers.service';
import { debounceTime, Subject } from 'rxjs';
import { AdditionalInformation } from './additional-information/additional-information';
import { BasicInformation } from './basic-information/basic-information';
import { ServicesAndItems } from './services-and-items/services-and-items';
import { CustomerSummary } from '../../models/customer-summary';
import { toDoc } from 'ngx-editor';
import { Quote } from '../../models/quote';
import { QuoteItems } from '../../models/quote-items';
import { QuoteFormPayload } from '../../models/quote-form-payload';
import { ErrorAlert } from '../../../../shared/ui/error-alert/error-alert';
import { TranslatePipe } from '@ngx-translate/core';
import { GREEK_AMOUNT_PATTERN, parseGreekAmount } from '../../../../shared/utils/currency.util';

@Component({
  selector: 'app-quotes-form',
  imports: [
    LoadingSpinner,
    ReactiveFormsModule,
    PrimaryButton,
    AdditionalInformation,
    BasicInformation,
    ServicesAndItems,
    ErrorAlert,
    TranslatePipe,
  ],
  templateUrl: './quotes-form.html',
  styleUrl: './quotes-form.css',
})
export class QuotesForm implements OnInit, AfterViewInit {
  @Input() formUsage: 'create' | 'edit' = 'create';
  @Input() isLoading: boolean = false;
  @Input() errorMessage?: string;
  @Output() onSubmit = new EventEmitter<any>();
  public quote?: Quote;
  public quoteItemUnits: string[] = [];
  public itemsErrorMessage?: string = undefined;
  public customerErrorMessage?: string = undefined;
  // Customers
  public customerSearch$ = new Subject<string>();
  public customersLoading: boolean = false;
  public customersList: CustomerSummary[] = [];
  // Parsed HTML data
  public notesHtml: string | null = null;
  public specialTermsHtml: string | null = null;
  // Form & Services
  private formBuilder: FormBuilder = inject(FormBuilder);
  quoteForm = this.formBuilder.group({
    customerId: new FormControl<number | null>(null, Validators.required),
    origin: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    destination: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    validityDays: new FormControl<string | null>(null, [
      Validators.required,
      Validators.min(1),
      Validators.pattern(/^\d+$/), // Only digits
    ]),
    items: this.formBuilder.array([]),
    notes: new FormControl<string | unknown | null>(null),
    specialTerms: new FormControl<string | unknown | null>(null),
  });
  private metadataService: MetadataService = inject(MetadataService);
  private customersService: CustomersService = inject(CustomersService);

  @Input() set quoteData(value: Quote | undefined) {
    if (!value) return;

    this.quote = value;

    // Remove old items
    const formItems = this.items;
    formItems.clear();

    // Update items
    value.quoteItems.forEach((item) => {
      const group = this.createItem();
      group.patchValue(item);
      formItems.push(group);
    });

    if (value.customer) {
      const exists = this.customersList.some((c) => c.id === value.customer.id);
      if (!exists) {
        const customerSummary: CustomerSummary = {
          id: value.customer.id,
          companyName: value.customer.companyName,
        };
        this.customersList = [customerSummary, ...this.customersList];
      }
    }

    // Update rest fields
    this.quoteForm.patchValue({
      customerId: value.customer.id,
      origin: value.origin,
      destination: value.destination,
      validityDays: value.validityDays.toString(),
      notes: value.notes ? toDoc(value.notes) : null,
      specialTerms: value.specialTerms ? toDoc(value.specialTerms) : null,
    });
  }

  public get validityDays(): FormControl {
    return this.quoteForm.get('validityDays') as FormControl;
  }

  public get items(): FormArray<FormGroup> {
    return this.quoteForm.get('items') as FormArray<FormGroup>;
  }

  ngOnInit(): void {
    // Fetch some inial customers
    if (this.formUsage === 'create') {
      this.fetchCustomers('');
    }

    this.metadataService.fetchQuoteItemUnits();
    this.metadataService.quoteItemUnits$.subscribe((units) => (this.quoteItemUnits = units));
  }

  ngAfterViewInit(): void {
    // Debouncer on customers search
    this.customerSearch$.pipe(debounceTime(300)).subscribe((value) => {
      this.fetchCustomers(value || '');
    });
  }

  public onClickAddItem(): void {
    this.items.push(this.createItem());
  }

  public onClickRemoveItem(index: number): void {
    this.items.removeAt(index);
  }

  public onSubmitClick(): void {
    if (this.quoteForm.invalid) {
      // Mark all controls as touched & re-run validators (recursively)
      this.markFormGroupRecursive(this.quoteForm);
      return;
    }

    if (this.items.length === 0) {
      this.onClickAddItem();
      this.markFormGroupRecursive(this.quoteForm);
      this.itemsErrorMessage = 'quotes.messages.required-service-items';
      return;
    }

    const raw = this.quoteForm.getRawValue();
    const parsedItems: QuoteItems[] = raw.items.map((item: any) => ({
      ...item,
      price: parseGreekAmount(item.price), // string "3.911,16" -> number 3911.16
    }));

    const payload: QuoteFormPayload = {
      customerId: raw.customerId!,
      origin: raw.origin,
      destination: raw.destination,
      validityDays: parseInt(this.validityDays.value),
      notes: this.notesHtml,
      specialTerms: this.specialTermsHtml,
      items: parsedItems,
    };

    this.onSubmit.emit(payload);
  }

  private createItem(): FormGroup {
    return this.formBuilder.group({
      name: new FormControl<string>('', { nonNullable: true, validators: Validators.required }),
      price: new FormControl<string | null>(null, [
        Validators.required,
        Validators.min(1),
        Validators.pattern(GREEK_AMOUNT_PATTERN),
      ]),
      quantity: new FormControl<number | null>(null, [
        Validators.required,
        Validators.min(1),
        Validators.pattern(/^\d+$/), // Only digits
      ]),
      unit: new FormControl<string | null>(null, Validators.required),
      description: new FormControl<string | null>(null),
    });
  }

  private fetchCustomers(customer: string): void {
    this.customersLoading = true;

    this.customersService.fetchCustomers({ companyName: customer }).subscribe({
      next: (res) => {
        this.customersLoading = false;
        this.customersList = res.data.content;
      },
      error: () => {
        this.customersLoading = false;
        this.customerErrorMessage = 'quotes.messages.failed-to-fetch-customers';
      },
    });
  }

  private markFormGroupRecursive(control: AbstractControl): void {
    if (control instanceof FormControl) {
      control.markAsTouched();
      control.updateValueAndValidity({ emitEvent: true });
    } else if (control instanceof FormGroup || control instanceof FormArray) {
      Object.values(control.controls).forEach((childControl) => {
        this.markFormGroupRecursive(childControl);
      });
    }
  }
}
