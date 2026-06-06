import { Component, inject } from '@angular/core';
import { VehicleForm } from '../vehicle-form/vehicle-form';
import { VehicleRequest } from '../models/vehicle-request';
import { VehiclesService } from '../vehicles.service';
import { Router } from '@angular/router';
import { LanguageService } from '../../core/services/language.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-create-vehicle',
  imports: [VehicleForm, TranslatePipe],
  templateUrl: './create-vehicle.html',
  styleUrl: './create-vehicle.css',
})
export class CreateVehicle {
  public isLoading: boolean = false;
  public errorMessage?: string;

  private router = inject(Router);
  private vehiclesService = inject(VehiclesService);
  private languageService = inject(LanguageService);

  public onSubmit(payload: VehicleRequest): void {
    this.isLoading = true;

    this.vehiclesService.createVehicle(payload).subscribe({
      next: () => {
        this.isLoading = false;
        this.languageService.toastSuccess('vehicles.messages.success-creation');
        this.router.navigate(['vehicles']);
      },
      error: (err) => {
        this.isLoading = false;

        if (err.status === 409) {
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
}
