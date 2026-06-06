import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/auth/services/auth.service';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-not-found',
  imports: [PrimaryButton, TranslatePipe],
  templateUrl: './not-found.html',
  styleUrl: './not-found.css',
})
export class NotFound {
  authService: AuthService = inject(AuthService);
}
