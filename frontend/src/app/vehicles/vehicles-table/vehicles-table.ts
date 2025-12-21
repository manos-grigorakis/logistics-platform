import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LoadingSpinner } from '../../shared/ui/loading-spinner/loading-spinner';
import { VehicleResponse } from '../models/vehicle-response';
import { RouterLink } from '@angular/router';
import { NgClass, DatePipe } from '@angular/common';
import { NgIcon } from '@ng-icons/core';

@Component({
  selector: 'app-vehicles-table',
  imports: [LoadingSpinner, RouterLink, NgClass, DatePipe, NgIcon],
  templateUrl: './vehicles-table.html',
  styleUrl: './vehicles-table.css',
})
export class VehiclesTable {
  @Input() isLoading?: boolean;
  @Input() vehicles?: VehicleResponse[];
  @Input() errorMessage?: string;
  @Output() onDeleteVehicle = new EventEmitter<number>();

  public onDeleteVehicleClick(id: number): void {
    this.onDeleteVehicle.emit(id);
  }

  public applyVehicleTypeBadgeColor(type: string): string {
    return type === 'truck' ? 'bg-primary-100 text-primary-800' : 'bg-purple-100  text-purple-800';
  }
}
