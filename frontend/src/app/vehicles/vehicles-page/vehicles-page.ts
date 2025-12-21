import { Component, inject, OnInit } from '@angular/core';
import { VehiclesService } from '../vehicles.service';
import { VehicleResponse } from '../models/vehicle-response';
import { VehiclesTable } from '../vehicles-table/vehicles-table';

@Component({
  selector: 'app-vehicles-page',
  imports: [VehiclesTable],
  templateUrl: './vehicles-page.html',
  styleUrl: './vehicles-page.css',
})
export class VehiclesPage implements OnInit {
  // UI
  public isLoading: boolean = false;
  public errorMessage?: string = undefined;

  // Vehicles
  public vehicles?: VehicleResponse[];

  // Services
  private vehiclesService = inject(VehiclesService);

  ngOnInit(): void {
    this.fetchVehicles();
  }

  private fetchVehicles(): void {
    this.isLoading = true;
    this.errorMessage = undefined;

    this.vehiclesService.fetchAllVehicles().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.vehicles = res;
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
