import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ShipmentsService } from '../shipments.service';
import { Shipment } from '../models/shipment';
import { DatePipe, NgClass, DecimalPipe } from '@angular/common';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { shipmentStatusBadgeColor } from '../utils/shipment-status-badge-color.utils';
import { ErrorAlert } from '../../shared/ui/error-alert/error-alert';

@Component({
  selector: 'app-view-shipment',
  imports: [LoadingSpinner, DatePipe, NgClass, ErrorAlert, DecimalPipe],
  templateUrl: './view-shipment.html',
  styleUrl: './view-shipment.css',
})
export class ViewShipment implements OnInit {
  private route = inject(ActivatedRoute);
  private shipmentsService = inject(ShipmentsService);

  public shipment?: Shipment;
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) return;
    this.fetchShipment(parseInt(tempId));
  }

  public shipmentStatusBadgeColor(status: string): string {
    return shipmentStatusBadgeColor(status);
  }

  private fetchShipment(id: number): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.shipmentsService.getShipment(id).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.shipment = res;
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.errorMessage = "Shipment doesn't exist";
        } else if (err.status === 500) {
          this.errorMessage = 'Server error. Please try again';
        } else {
          this.errorMessage = 'An error occurred. Please try again';
        }
      },
    });
  }
}
