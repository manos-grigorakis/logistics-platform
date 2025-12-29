import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ShipmentsService } from '../shipments.service';
import { Shipment } from '../models/shipment';
import { ShipmentsTable } from '../shipments-table/shipments-table';
import { Pagination } from '../../shared/ui/pagination/pagination';
import { ShipmentParams } from '../models/shipment-params';
import { FiltersWrapper } from '../../shared/ui/filters-wrapper/filters-wrapper';
import { ShipmentsFilters } from '../shipments-filters/shipments-filters';
import { MetadataService } from '../../metadata/metadata.service';
import { debounceTime, distinctUntilChanged, Subject, Subscription } from 'rxjs';
import { ShipmentStatus } from '../models/shipment-status';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-shipments-page',
  imports: [ShipmentsTable, Pagination, FiltersWrapper, ShipmentsFilters],
  templateUrl: './shipments-page.html',
  styleUrl: './shipments-page.css',
})
export class ShipmentsPage implements OnInit, OnDestroy {
  public shipments: Shipment[] = [];
  public isLoading: boolean = false;
  public errorMessage?: string;

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  public pageSize: number = 0;

  // Shipment statuses
  public statuses: ShipmentStatus[] = [];
  private statusesSub?: Subscription;

  // Filtering & Sorting
  public filterLabel: string = 'Filter by';
  public sortLabel: string = 'Sort by';

  // Dates
  private pickupTo?: string;
  private pickupFrom?: string;

  // Search
  private searchChanged$ = new Subject<string>();

  // Services
  private shipmentsService = inject(ShipmentsService);
  private metadataService = inject(MetadataService);

  private currentParams: ShipmentParams = {
    page: 0,
  };

  ngOnInit(): void {
    this.fetchShipments();
    this.metadataService.fetchShipmentsStatuses();

    this.statusesSub = this.fetchShipmentsStatuses();

    // Debouncer
    this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((value) => this.onSearch(value));
  }

  ngOnDestroy(): void {
    this.statusesSub?.unsubscribe();
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.fetchShipments({ page: page });
  }

  public onSearchChanged(value: string): void {
    this.searchChanged$.next(value);
  }

  public onSearch(value: string): void {
    let param = value.trim();

    if (param.length === 0) {
      this.fetchShipments({
        page: 0,
        number: undefined,
        sortBy: undefined,
        sortDirection: undefined,
        status: undefined,
      });
    }

    const minSearchByNumber = 6;
    const normalized = param.toUpperCase();

    if (normalized && param.length < minSearchByNumber) return;

    this.fetchShipments({
      page: 0,
      number: normalized,
      sortBy: undefined,
      sortDirection: undefined,
      status: undefined,
    });
  }

  public onRefresh(): void {
    this.fetchShipments();
  }

  public onSort(value: string): void {
    switch (value) {
      case 'sort-all':
        this.sortLabel = 'Sort by';
        this.fetchShipments({ page: 0, sortBy: undefined, sortDirection: undefined });
        break;
      case 'sort-asc-by-number':
        this.sortLabel = 'Number (A-Z)';
        this.fetchShipments({ page: 0, sortBy: 'number', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-number':
        this.sortLabel = 'Number (Z-A)';
        this.fetchShipments({ page: 0, sortBy: 'number', sortDirection: 'desc' });
        break;
      case 'sort-asc-by-pickup':
        this.sortLabel = 'Pickup (A-Z)';
        this.fetchShipments({ page: 0, sortBy: 'pickup', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-pickup':
        this.sortLabel = 'Pickup (Z-A)';
        this.fetchShipments({ page: 0, sortBy: 'pickup', sortDirection: 'desc' });
        break;
    }
  }

  public onFilter(value: string): void {
    if (value === 'filter-by-all') {
      this.filterLabel = 'Filter by';
      this.fetchShipments({ page: 0, status: undefined, sortDirection: undefined });
      return;
    }

    const prefix = 'filter-by-shipment-status-';

    if (value.startsWith(prefix)) {
      const status = value.slice(prefix.length);
      this.filterLabel = status.charAt(0).toUpperCase() + status.slice(1);
      this.fetchShipments({ status: status.toUpperCase() });
    }
  }

  public onPickupFrom(value: string): void {
    this.pickupFrom = value || undefined;
    this.handleDatesSorting();
  }

  public onPickupTo(value: string): void {
    this.pickupTo = value || undefined;
    this.handleDatesSorting();
  }

  public handleDatesSorting(): void {
    const from = this.pickupFrom;
    const to = this.pickupTo;

    if (from && to) {
      const fromDate = new Date(from);
      const toDate = new Date(to);

      if (fromDate > toDate) {
        toast.error('From must be before To');
        return;
      }
    }

    this.fetchShipments({
      page: 0,
      pickupFrom: this.pickupFrom || undefined,
      pickupTo: this.pickupTo || undefined,
    });
  }

  private fetchShipments(params?: ShipmentParams): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    // Merge current state params with new params
    const finalParams: ShipmentParams = {
      ...this.currentParams,
      ...params,
    };

    // Saved for future requests
    this.currentParams = finalParams;

    this.shipmentsService.fetchShipments(finalParams).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.shipments = res.content;

        // pagination
        this.currentPage = res.number;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
        this.pageSize = res.size;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occured. Please try again';
        }
      },
    });
  }

  private fetchShipmentsStatuses(): Subscription {
    return this.metadataService.shipmentStatuses$.subscribe({
      next: (data) => {
        this.statuses = [...data];
      },
      error: (err) => console.error(err),
    });
  }
}
