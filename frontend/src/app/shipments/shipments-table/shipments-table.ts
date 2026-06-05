import { Component, Input } from '@angular/core';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { NgClass, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Shipment } from '../models/shipment';
import { shipmentStatusBadgeColor } from '../utils/shipment-status-badge-color.utils';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-shipments-table',
  imports: [LoadingSpinner, NgClass, RouterLink, DatePipe, TranslatePipe],
  templateUrl: './shipments-table.html',
  styleUrl: './shipments-table.css',
})
export class ShipmentsTable {
  @Input() shipments: Shipment[] = [];
  @Input() isLoading?: boolean;
  @Input() errorMessage?: string;

  public shipmentStatusBadgeColor(status: string): string {
    return shipmentStatusBadgeColor(status);
  }
}
