import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ShipmentsFilters } from '../../../../shipments/shipments-filters/shipments-filters';
import { ShipmentsCard } from './shipments-card/shipments-card';
import { Shipment } from '../../../../shipments/models/shipment';
import { ShipmentsService } from '../../../../shipments/shipments.service';
import { ErrorAlert } from '../../../../shared/ui/error-alert/error-alert';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { Pagination } from '../../../../shared/ui/pagination/pagination';
import { ActivatedRoute } from '@angular/router';
import { NgClass } from '@angular/common';
import { MetadataService } from '../../../../metadata/metadata.service';
import { debounceTime, distinctUntilChanged, Subject, Subscription } from 'rxjs';
import { ShipmentStatus } from '../../../../shipments/models/shipment-status';
import { ShipmentParams } from '../../../../shipments/models/shipment-params';

@Component({
  selector: 'app-customer-tab-shipments',
  imports: [ShipmentsFilters, ShipmentsCard, ErrorAlert, LoadingSpinner, Pagination, NgClass],
  templateUrl: './customer-tab-shipments.html',
  styleUrl: './customer-tab-shipments.css',
})
export class CustomerTabShipments implements OnInit, OnDestroy {
  private shipmentsService = inject(ShipmentsService);
  private metadataService = inject(MetadataService);
  private route = inject(ActivatedRoute);
  private customerId?: number;

  public isLoading: boolean = false;
  public errorMessage?: string = undefined;
  public shipments: Shipment[] = [];

  // Filters | Sorting | Search
  public activeSortLabel: string = 'Sort by';
  public activeFilterLabel: string = 'Filter by';
  public filterLabel: string = 'Filter by';
  public sortLabel: string = 'Sort by';
  private searchChanged$ = new Subject<string>();
  private subSearch$?: Subscription;
  private currentParams: ShipmentParams = {
    page: 0,
  };

  // Shipment statuses
  public statuses: ShipmentStatus[] = [];
  private statusesSub?: Subscription;

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  private shipmentsToShow: number = 5;
  public pageSize: number = this.shipmentsToShow;

  ngOnInit(): void {
    let tempId = this.route.parent?.snapshot.paramMap.get('id');
    if (!tempId) return;
    this.customerId = parseInt(tempId);

    this.fetchShipmentsByCustomer({ size: this.shipmentsToShow });

    // Metadata for statuses filter
    this.statusesSub = this.metadataService.fetchShipmentsStatuses().subscribe((statuses) => {
      this.statuses = statuses;
    });

    // Debouncer for search
    this.subSearch$ = this.searchChanged$
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((value) => this.onSearch(value));
  }

  ngOnDestroy(): void {
    this.statusesSub?.unsubscribe();
    this.subSearch$?.unsubscribe();
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.fetchShipmentsByCustomer({ page: page, size: this.shipmentsToShow });
  }

  public onSearchChanged(value: string): void {
    this.searchChanged$.next(value);
  }

  public onSearch(value: string): void {
    let param = value.trim();

    if (param.length === 0) {
      this.fetchShipmentsByCustomer({
        page: 0,
        number: undefined,
        sortBy: undefined,
        sortDirection: undefined,
        status: undefined,
      });
      return;
    }

    const minSearchByNumber = 6;
    const normalized = param.toUpperCase();

    if (normalized && param.length < minSearchByNumber) return;

    this.fetchShipmentsByCustomer({
      page: 0,
      number: normalized,
      sortBy: undefined,
      sortDirection: undefined,
      status: undefined,
    });
  }

  // Filters
  public onRefreshClick(): void {
    this.fetchShipmentsByCustomer();
  }

  public onSort(value: string): void {
    switch (value) {
      case 'sort-all':
        this.sortLabel = 'Sort by';
        this.fetchShipmentsByCustomer({ page: 0, sortBy: undefined, sortDirection: undefined });
        break;
      case 'sort-asc-by-number':
        this.sortLabel = 'Number (A-Z)';
        this.fetchShipmentsByCustomer({ page: 0, sortBy: 'number', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-number':
        this.sortLabel = 'Number (Z-A)';
        this.fetchShipmentsByCustomer({ page: 0, sortBy: 'number', sortDirection: 'desc' });
        break;
      case 'sort-asc-by-pickup':
        this.sortLabel = 'Pickup (A-Z)';
        this.fetchShipmentsByCustomer({ page: 0, sortBy: 'pickup', sortDirection: 'asc' });
        break;
      case 'sort-desc-by-pickup':
        this.sortLabel = 'Pickup (Z-A)';
        this.fetchShipmentsByCustomer({ page: 0, sortBy: 'pickup', sortDirection: 'desc' });
        break;
    }
  }

  public onFilter(value: string): void {
    if (value === 'filter-by-all') {
      this.filterLabel = 'Filter by';
      this.fetchShipmentsByCustomer({ page: 0, status: undefined, sortDirection: undefined });
      return;
    }

    const prefix = 'filter-by-shipment-status-';

    if (value.startsWith(prefix)) {
      const status = value.slice(prefix.length);
      this.filterLabel = status.charAt(0).toUpperCase() + status.slice(1);
      this.fetchShipmentsByCustomer({ status: status.toUpperCase() });
    }
  }

  private fetchShipmentsByCustomer(params?: ShipmentParams): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    // Merge current state params with new params
    const finalParams: ShipmentParams = {
      ...this.currentParams,
      ...params,
      customerId: this.customerId,
    };

    // Saved for future requests
    this.currentParams = finalParams;

    this.shipmentsService.fetchShipments(finalParams).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.shipments = res.content;

        // Pagination
        this.currentPage = res.number;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occurred. Please try again';
        }
      },
    });
  }
}
