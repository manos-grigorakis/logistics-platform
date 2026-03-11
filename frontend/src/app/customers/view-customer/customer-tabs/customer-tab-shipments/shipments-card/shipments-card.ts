import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Shipment } from '../../../../../shipments/models/shipment';
import { shipmentStatusBadgeColor } from '../../../../../shipments/utils/shipment-status-badge-color.utils';
import { NgClass, DatePipe, TitleCasePipe } from '@angular/common';
import { ShipmentStatus } from '../../../../../shipments/models/shipment-status';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-shipments-card',
  imports: [NgClass, DatePipe, TitleCasePipe, FormsModule],
  templateUrl: './shipments-card.html',
  styleUrl: './shipments-card.css',
})
export class ShipmentsCard {
  @Input() shipment!: Shipment;
  @Input() shipmentStatuses!: ShipmentStatus[];
  @Input() pendingShipmentStatus?: string;
  @Input() editingShipmentId?: number;
  @Output() onStatus = new EventEmitter<{
    shipment: Shipment;
    newStatus: string;
  }>();

  public shipmentStatusBadgeColor(status: string): string {
    return shipmentStatusBadgeColor(status);
  }

  public onStatusChange(shipment: Shipment, newStatus: string): void {
    this.onStatus.emit({ shipment, newStatus });
  }
}
