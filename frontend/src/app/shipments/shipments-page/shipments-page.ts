import { Component, inject, OnInit } from '@angular/core';
import { ShipmentsService } from '../shipments.service';
import { Shipment } from '../models/shipment';
import { ShipmentsTable } from '../shipments-table/shipments-table';
import { Pagination } from '../../shared/ui/pagination/pagination';
import { ShipmentParams } from '../models/shipment-params';

@Component({
  selector: 'app-shipments-page',
  imports: [ShipmentsTable, Pagination],
  templateUrl: './shipments-page.html',
  styleUrl: './shipments-page.css',
})
export class ShipmentsPage implements OnInit {
  public shipments: Shipment[] = [];
  public isLoading: boolean = false;
  public errorMessage?: string;

  // Pagination
  public currentPage: number = 0;
  public totalPages: number = 0;
  public totalElements: number = 0;
  public isFirstPage: boolean = false;
  public pageSize: number = 0;

  private shipmentsService = inject(ShipmentsService);

  private currentParams: ShipmentParams = {
    page: 0,
  };

  ngOnInit(): void {
    this.fetchShipments();
  }

  public onPageChange(page: number): void {
    if (page === this.currentPage) return;

    this.currentPage = page;
    this.fetchShipments({ page: page });
  }

  private fetchShipments(params?: ShipmentParams): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    // Merge current state params with new params
    const finalParams: ShipmentParams = {
      ...this.currentParams,
      ...params,
    };

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
}
