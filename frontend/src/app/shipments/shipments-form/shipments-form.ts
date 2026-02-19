import { Component, EventEmitter, inject, Input, OnInit, Output, ViewChild } from '@angular/core';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { ErrorAlert } from '../../shared/ui/error-alert/error-alert';
import { FormBuilder, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgSelectComponent } from '@ng-select/ng-select';
import { map, Subject } from 'rxjs';
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

@Component({
  selector: 'app-shipments-form',
  imports: [
    LoadingSpinner,
    ErrorAlert,
    ReactiveFormsModule,
    NgSelectComponent,
    PrimaryButton,
    MainInput,
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

  // UI
  public uiErrorMessage?: string = undefined;

  // Quotes
  public quotesLoading: boolean = false;
  public quoteSearch$ = new Subject<string>();
  public quotesList: QuoteSummary[] = [];

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

  shipmentForm = this.formBuilder.group({
    quoteId: new FormControl<number | null>(null, {
      nonNullable: true,
      validators: [Validators.required],
    }),
    driverId: new FormControl<number | null>(null),
    truckId: new FormControl<number | null>(null),
    trailerId: new FormControl<number | null>(null),
    status: new FormControl<string>(''),
    pickup: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    notes: new FormControl<string>(''),
  });

  ngOnInit(): void {
    this.fetchQuotes();
    this.fetchDrivers();
    this.fetchVehicles();
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

  public onSubmitClick(): void {
    if (this.shipmentForm.invalid) {
      // Mark all controls as touched & re-run validators
      Object.values(this.shipmentForm.controls).forEach((control) => {
        control.markAsTouched();
        control.updateValueAndValidity({ emitEvent: true });
      });
      return;
    }

    const raw = this.shipmentForm.getRawValue();

    if (raw.quoteId === null) return;

    const payload: ShipmentPayload = {
      quoteId: raw.quoteId,
      createdByUserId: this.authService.getUserId()!,
      pickup: new Date(raw.pickup),
      driverId: raw.driverId,
      truckId: raw.truckId ? raw.truckId : null,
      trailerId: raw.trailerId ? raw.trailerId : null,
      notes: raw.notes ? raw.notes : null,
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

  private fetchQuotes(): void {
    this.quotesLoading = true;
    this.uiErrorMessage = undefined;

    this.quotesService.fetchQuotes({ quoteStatus: 'ACCEPTED' }).subscribe({
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

  private fetchDrivers(): void {
    this.driverLoading = true;
    this.uiErrorMessage = undefined;

    this.usersService
      .fetchUsers()
      // TODO: Update to check if role is driver and not based on the ID
      .pipe(map((users) => users.filter((u) => u.roleId === 2)))
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
}
