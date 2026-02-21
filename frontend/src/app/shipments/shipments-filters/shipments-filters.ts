import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SearchBar } from '../../shared/forms/search-bar/search-bar';
import { RoundedIconButton } from '../../shared/forms/rounded-icon-button/rounded-icon-button';
import { NgIcon } from '@ng-icons/core';
import { RouterLink } from '@angular/router';
import { DropdownButton } from '../../shared/ui/dropdown-button/dropdown-button';
import { ShipmentStatus } from '../models/shipment-status';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-shipments-filters',
  imports: [SearchBar, RoundedIconButton, DropdownButton, NgIcon, RouterLink, FormsModule],
  templateUrl: './shipments-filters.html',
  styleUrl: './shipments-filters.css',
})
export class ShipmentsFilters {
  @Input() showCreateButton: boolean = true;
  @Input() showDatesFilters: boolean = true;
  @Input() isLoading: boolean = false;
  @Input() filterLabel?: string;
  @Input() sortLabel?: string;
  @Input() shipmentStatuses: ShipmentStatus[] = [];
  @Output() refresh = new EventEmitter<void>();
  @Output() searchChanged = new EventEmitter<string>();
  @Output() sortBy = new EventEmitter<string>();
  @Output() filterBy = new EventEmitter<string>();
  @Output() onPickupFrom = new EventEmitter<string>();
  @Output() onPickupTo = new EventEmitter<string>();

  public searchTerm: string = '';
  public pickupFrom: string = '';
  public pickupTo: string = '';

  public loadShipments(): void {
    this.refresh.emit();
  }

  public onSearchChange(value: string): void {
    this.searchTerm = value;
    this.searchChanged.emit(value);
  }

  public onSortByFieldClick(sortOption: string): void {
    this.sortBy.emit(sortOption);
  }

  public onFilterByFieldClick(filterOption: string): void {
    this.filterBy.emit(filterOption);
  }

  public onPickupFromChange(value: string) {
    this.onPickupFrom.emit(value);
  }

  public onPickupToChange(value: string) {
    this.onPickupTo.emit(value);
  }
}
