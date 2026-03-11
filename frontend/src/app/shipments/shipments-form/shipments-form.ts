import { Component, EventEmitter, inject, Input, OnInit, Output, ViewChild } from '@angular/core';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { ErrorAlert } from '../../shared/ui/error-alert/error-alert';
import {
  FormBuilder,
  FormControl,
  Validators,
  ReactiveFormsModule,
  FormArray,
  FormGroup,
  AbstractControl,
} from '@angular/forms';
import { NgSelectComponent } from '@ng-select/ng-select';
import { debounceTime, distinctUntilChanged, finalize, map, Subject, switchMap, tap } from 'rxjs';
import { QuoteSummary } from '../models/quote-summary';
import { QuotesService } from '../../quotes/quotes.service';
import { DriversSummary } from '../models/drivers-summary';
import { UsersService } from '../../users/users.service';
import { VehiclesSummary } from '../models/vehicles-summary';
import { VehiclesService } from '../../vehicles/vehicles.service';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { MainInput } from '../../shared/forms/main-input/main-input';
import { ShipmentPayload } from '../models/shipment-payload';
import { AuthService } from '../../auth/services/auth.service';
import { Shipment } from '../models/shipment';
import { MetadataService } from '../../metadata/metadata.service';
import { LowerCasePipe, TitleCasePipe, NgClass } from '@angular/common';
import { RoundedIconButton } from '../../shared/forms/rounded-icon-button/rounded-icon-button';
import { CargoItems } from '../models/cargo-items';

@Component({
  selector: 'app-shipments-form',
  imports: [
    LoadingSpinner,
    ErrorAlert,
    ReactiveFormsModule,
    NgSelectComponent,
    PrimaryButton,
    MainInput,
    LowerCasePipe,
    TitleCasePipe,
    RoundedIconButton,
    NgClass,
  ],
  templateUrl: './shipments-form.html',
  styleUrl: './shipments-form.css',
})
export class ShipmentsForm implements OnInit {
  @Input() formUsage: 'create' | 'edit' = 'create';
  @Input() isLoading?: boolean;
  @Input() errorMessage?: string;
  @Output() onSubmit = new EventEmitter<ShipmentPayload>();

  @ViewChild('quoteIdSelect') quoteIdSelect!: any;
  @ViewChild('driverIdSelect') driverIdSelect!: any;
  @ViewChild('truckIdSelect') truckIdSelect!: any;
  @ViewChild('trailerIdSelect') trailerIdSelect!: any;

  private formBuilder = inject(FormBuilder);
  private quotesService = inject(QuotesService);
  private usersService = inject(UsersService);
  private vehiclesService = inject(VehiclesService);
  private authService = inject(AuthService);
  private metadataService = inject(MetadataService);

  // UI
  public uiErrorMessage?: string = undefined;
  public isFormSubmitted: boolean = false;

  // Quotes
  public quotesLoading: boolean = false;
  public quoteSearch$ = new Subject<string>();
  public quotesList: QuoteSummary[] = [];
  private readonly QUOTE_STATUS: string = 'ACCEPTED';

  // Drivers
  public driverLoading: boolean = false;
  public driverSearch$ = new Subject<string>();
  public driversList: DriversSummary[] = [];

  // Truck
  public truckLoading: boolean = false;
  public truckSearch$ = new Subject<string>();
  public truckList: VehiclesSummary[] = [];

  // Trailer
  public trailerLoading: boolean = false;
  public trailerSearch$ = new Subject<string>();
  public trailerList: VehiclesSummary[] = [];

  // CargoItems
  public cargoItemsUnits: string[] = [];

  shipmentForm = this.formBuilder.group({
    quoteId: new FormControl<number | null>(null, {
      nonNullable: true,
      validators: [Validators.required],
    }),
    driverId: new FormControl<number | null>(null),
    truckId: new FormControl<number | null>(null),
    trailerId: new FormControl<number | null>(null),
    status: new FormControl<string>(''),
    pickup: new FormControl<string>('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.pattern(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/), // yyyy-MM-ddTHH:mm
      ],
    }),
    notes: new FormControl<string>(''),
    cargoItems: this.formBuilder.array([]),
  });

  @Input() set shipmentsData(value: Shipment | undefined) {
    if (!value) return;

    // Remove old cargo items
    const cargoItems = this.cargoItems;
    cargoItems.clear();

    // Update cargo items
    value.cargoItems?.forEach((item) => {
      const group = this.createCargoItem();
      group.patchValue(item);
      cargoItems.push(group);
    });

    this.shipmentForm.patchValue({
      quoteId: value.quote.id,
      driverId: value.driver?.id,
      truckId: value.truck?.id,
      trailerId: value.trailer?.id,
      pickup: value.pickup.slice(0, 16), // yyyy-MM-ddTHH:mm
      notes: value.notes,
    });

    // Update dropdown for quote
    this.quotesList = [value.quote, ...this.quotesList];
  }

  ngOnInit(): void {
    if (this.formUsage === 'edit') this.quoteId.disable();

    this.fetchQuotes();
    this.fetchDrivers();
    this.fetchVehicles();

    this.metadataService.fetchCargoItemsUnits();
    this.metadataService.cargoItemUnits$.subscribe((units) => (this.cargoItemsUnits = units));

    this.fetchQuotesWithSearch();
  }

  public get quoteId(): FormControl {
    return this.shipmentForm.get('quoteId') as FormControl;
  }

  public get driverId(): FormControl {
    return this.shipmentForm.get('driverId') as FormControl;
  }

  public get truckId(): FormControl {
    return this.shipmentForm.get('truckId') as FormControl;
  }

  public get trailerId(): FormControl {
    return this.shipmentForm.get('trailerId') as FormControl;
  }

  public get status(): FormControl {
    return this.shipmentForm.get('status') as FormControl;
  }

  public get pickup(): FormControl {
    return this.shipmentForm.get('pickup') as FormControl;
  }

  public get notes(): FormControl {
    return this.shipmentForm.get('notes') as FormControl;
  }

  public get cargoItems(): FormArray<FormGroup> {
    return this.shipmentForm.get('cargoItems') as FormArray<FormGroup>;
  }

  public onSubmitClick(): void {
    this.isFormSubmitted = true;

    if (this.shipmentForm.invalid) {
      // Mark all controls as touched & re-run validators
      this.markFormGroupRecursive(this.shipmentForm);
      return;
    }

    const raw = this.shipmentForm.getRawValue();

    if (raw.quoteId === null) return;

    const payload: ShipmentPayload = {
      quoteId: raw.quoteId,
      createdByUserId: this.authService.getUserId()!,
      pickup: `${raw.pickup}:00`,
      driverId: raw.driverId ? raw.driverId : null,
      truckId: raw.truckId ? raw.truckId : null,
      trailerId: raw.trailerId ? raw.trailerId : null,
      notes: raw.notes ? raw.notes : null,
      cargoItems: raw.cargoItems as CargoItems[],
    };

    this.onSubmit.emit(payload);
  }

  // Focus on selects
  public onClickFocusQuoteIdSelect(): void {
    this.quoteIdSelect.focus();
  }

  public onClickFocusDriverIdSelect(): void {
    this.driverIdSelect.focus();
  }

  public onClickFocusTruckIdSelect(): void {
    this.truckIdSelect.focus();
  }

  public onClickFocusTrailerIdSelect(): void {
    this.trailerIdSelect.focus();
  }

  // Cargo Items
  public onClickAddCargoItem(): void {
    this.cargoItems.push(this.createCargoItem());
    this.isFormSubmitted = false;
  }

  public onClickRemoveCargoIndex(index: number): void {
    this.cargoItems.removeAt(index);
  }

  public getCargoItemUnitControl(index: number): FormControl<string | null> {
    return this.cargoItems.at(index).get('unit') as FormControl<string | null>;
  }

  public hasCargoItemsErrors(): boolean {
    return this.cargoItems.controls.some((control) => control.invalid);
  }

  private fetchQuotes(): void {
    this.quotesLoading = true;
    this.uiErrorMessage = undefined;

    this.quotesService.fetchQuotes({ quoteStatus: this.QUOTE_STATUS }).subscribe({
      next: (res) => {
        this.quotesLoading = false;

        this.quotesList = res.content.map((quote) => ({
          id: quote.id,
          number: quote.number,
        }));
      },
      error: (err) => {
        this.quotesLoading = false;

        if (err.status === 500) {
          this.uiErrorMessage = 'Server error. Please try again';
        } else {
          this.uiErrorMessage = 'An error occurred. Please try again';
        }
      },
    });
  }

  private fetchQuotesWithSearch(): void {
    this.uiErrorMessage = undefined;

    this.quoteSearch$
      .pipe(
        tap(() => (this.quotesLoading = true)),
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((search) =>
          this.quotesService
            .fetchQuotes({
              quoteStatus: this.QUOTE_STATUS,
              number: search ?? undefined,
            })
            .pipe(finalize(() => (this.quotesLoading = false))),
        ),
      )
      .subscribe({
        next: (res) => (this.quotesList = [...res.content]),
        error: (err) => {
          if (err.status === 500) {
            this.uiErrorMessage = 'Server error. Please try again';
          } else {
            this.uiErrorMessage = 'An error occurred. Please try again';
          }
        },
      });
  }

  private fetchDrivers(): void {
    this.driverLoading = true;
    this.uiErrorMessage = undefined;

    this.usersService
      .fetchUsers()
      .pipe(map((users) => users.filter((u) => u.roleName === 'DRIVER')))
      .subscribe({
        next: (res) => {
          this.driverLoading = false;

          this.driversList = res.map((driver) => ({
            id: driver.id,
            fullName: driver.firstName + ' ' + driver.lastName,
          }));
        },
        error: (err) => {
          this.driverLoading = false;

          if (err.status === 500) {
            this.uiErrorMessage = 'Server error. Please try again';
          } else {
            this.uiErrorMessage = 'An error occurred. Please try again';
          }
        },
      });
  }

  private fetchVehicles(): void {
    this.truckLoading = true;
    this.trailerLoading = true;
    this.uiErrorMessage = undefined;

    this.vehiclesService
      .fetchAllVehicles()
      .pipe(
        map((vehicles) => ({
          truck: vehicles.filter((v) => v.type === 'truck'),
          trailer: vehicles.filter((v) => v.type === 'trailer'),
        })),
      )
      .subscribe({
        next: (res) => {
          this.truckLoading = false;
          this.trailerLoading = false;
          this.truckList = res.truck;
          this.trailerList = res.trailer;
        },
        error: (err) => {
          this.truckLoading = false;
          this.trailerLoading = false;

          if (err.status === 500) {
            this.uiErrorMessage = 'Server error. Please try again';
          } else {
            this.uiErrorMessage = 'An error occurred. Please try again';
          }
        },
      });
  }

  private createCargoItem(): FormGroup {
    return this.formBuilder.group({
      description: new FormControl<string | null>(null, Validators.required),
      unit: new FormControl<string | null>(null, Validators.required),
      quantity: new FormControl<number | null>(null, [
        Validators.required,
        Validators.min(1),
        Validators.pattern(/^\d+$/), // Only digits
      ]),
      weightKg: new FormControl<number | null>(null, [
        Validators.required,
        Validators.pattern(/^\d+(\.\d{1,2})?$/), // Only digits and two decimals
      ]),
      volumeM3: new FormControl<number | null>(null, Validators.pattern(/^\d+(\.\d{1,2})?$/)), // Only digits and two decimals
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
