import { Component, Input } from '@angular/core';
import { Shipment } from '../../../../../shipments/models/shipment';
import { shipmentStatusBadgeColor } from '../../../../../shipments/utils/shipment-status-badge-color.utils';
import { NgClass, DatePipe } from '@angular/common';

@Component({
  selector: 'app-shipments-card',
  imports: [NgClass, DatePipe],
  templateUrl: './shipments-card.html',
  styleUrl: './shipments-card.css',
})
export class ShipmentsCard {
  @Input() shipment!: Shipment;

  public shipmentStatusBadgeColor(status: string): string {
    return shipmentStatusBadgeColor(status);
  }
}
