import { Component, inject, OnInit } from '@angular/core';
import { VehicleForm } from '../../components/vehicle-form/vehicle-form';
import { VehiclesService } from '../../vehicles.service';
import { ActivatedRoute, Router } from '@angular/router';
import { VehicleResponse } from '../../models/vehicle-response';
import { VehicleRequest } from '../../models/vehicle-request';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../../../core/services/language.service';

@Component({
  selector: 'app-edit-vehicle',
  imports: [VehicleForm, TranslatePipe],
  templateUrl: './edit-vehicle.html',
  styleUrl: './edit-vehicle.css',
})
export class EditVehicle implements OnInit {
  public isLoading: boolean = false;
  public vehicle?: VehicleResponse;
  public errorMessage?: string;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private vehiclesService = inject(VehiclesService);
  private languageService = inject(LanguageService);
  private id?: number;

  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.languageService.toastError('vehicles.messages.not-found');
      this.router.navigate(['vehicles']);
      return;
    }

    this.id = parseInt(id);
    this.fetchSelectedVehicle(this.id);
  }

  public onSubmitForm(payload: VehicleRequest): void {
    if (!this.id) return;

    this.isLoading = true;

    this.vehiclesService.updateVehicle(this.id, payload).subscribe({
      next: () => {
        this.isLoading = false;
        this.languageService.toastSuccess('vehicles.messages.success-update');
        this.router.navigate(['vehicles']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 404) {
          this.errorMessage = 'vehicles.messages.not-found';
        } else if (err.status === 409) {
          this.errorMessage = this.languageService.translateKey(
            'vehicles.messages.exists-by-plate',
            { plate: payload.plate },
          );
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }

  private fetchSelectedVehicle(id: number): void {
    this.vehiclesService.fetchVehicleById(id).subscribe({
      next: (res) => {
        this.vehicle = res.data;
      },
      error: (err) => {
        if (err.status === 404) {
          this.languageService.toastError('vehicles.messages.not-found');
          this.router.navigate(['vehicles']);
        } else if (err.status === 500) {
          this.languageService.toastError('common.errors.server');
        } else {
          this.languageService.toastError('common.errors.generic');
        }
      },
    });
  }
}
