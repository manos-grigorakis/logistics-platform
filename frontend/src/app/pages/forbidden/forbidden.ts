import { Component, inject } from '@angular/core';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { AuthService } from '../../core/auth/services/auth.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-forbidden',
  imports: [PrimaryButton, TranslatePipe],
  templateUrl: './forbidden.html',
  styleUrl: './forbidden.css',
})
export class Forbidden {
  public authService: AuthService = inject(AuthService);
}
