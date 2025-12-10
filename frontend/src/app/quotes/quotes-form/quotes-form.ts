import {
  AfterViewInit,
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { ReactiveFormsModule } from '@angular/forms';
import { MetadataService } from '../../metadata/metadata.service';
import { CustomersService } from '../../customers/customers.service';
import { Customer } from '../../customers/models/customer';
import { debounceTime, Subject } from 'rxjs';
import { AdditionalInformation } from './additional-information/additional-information';
import { BasicInformation } from './basic-information/basic-information';
import { ServicesAndItems } from './services-and-items/services-and-items';

@Component({
  selector: 'app-quotes-form',
  imports: [
    LoadingSpinner,
    ReactiveFormsModule,
    PrimaryButton,
    AdditionalInformation,
    BasicInformation,
    ServicesAndItems,
  ],
  templateUrl: './quotes-form.html',
  styleUrl: './quotes-form.css',
})
export class QuotesForm implements OnInit, AfterViewInit {
  @Input() formUsage: 'create' | 'edit' = 'create';
  @Input() isLoading: boolean = false;
  @Input() errorMessage?: string;
  @Output() onSubmit = new EventEmitter<any>();

  // Form & Services
  private formBuilder: FormBuilder = inject(FormBuilder);
  private metadataService: MetadataService = inject(MetadataService);
  private customersService: CustomersService = inject(CustomersService);

  public quoteItemUnits: string[] = [];
  public itemsErrorMessage?: string = undefined;
  public customerErrorMessage?: string = undefined;

  // Customers
  public customerSearch$ = new Subject<string>();
  public customersLoading: boolean = false;
  public customersList: Customer[] = [];

  // Parsed HTML data
  public notesHtml: string | null = null;
  public specialTermsHtml: string | null = null;

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
    validityDays: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(1),
      Validators.pattern(/^\d+$/), // Only digits
    ]),
    notes: new FormControl<string | null>(null),
    items: this.formBuilder.array([]),
    specialTerms: new FormControl<string | null>(null),
  });

  ngOnInit(): void {
    // Fetch some inial customers
    this.fetchCustomers('');

    this.metadataService.fetchQuoteItemUnits();
    this.metadataService.quoteItemUnits$.subscribe((units) => (this.quoteItemUnits = units));
  }

  ngAfterViewInit(): void {
    // Debouncer on customers search
    this.customerSearch$.pipe(debounceTime(300)).subscribe((value) => {
      this.fetchCustomers(value || '');
    });
  }

  public get validityDays(): FormControl {
    return this.quoteForm.get('validityDays') as FormControl;
  }

  public get items(): FormArray<FormGroup> {
    return this.quoteForm.get('items') as FormArray<FormGroup>;
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
      this.itemsErrorMessage = 'Service/Items are required';
      return;
    }

    const raw = this.quoteForm.getRawValue();
    const payload = {
      ...raw,
      validityDays: parseInt(this.validityDays.value),
      notes: this.notesHtml,
      specialTerms: this.specialTermsHtml,
    };

    this.onSubmit.emit(payload);
  }

  private createItem(): FormGroup {
    return this.formBuilder.group({
      name: new FormControl<string>('', { nonNullable: true, validators: Validators.required }),
      price: new FormControl<number | null>(null, [
        Validators.required,
        Validators.min(1),
        Validators.pattern(/^\d+(\.\d{1,2})?$/), // Only digits and two decimals
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
        this.customersList = res.content;
      },
      error: (err) => {
        this.customersLoading = false;
        this.customerErrorMessage = 'Failed to fetch Customers';
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
