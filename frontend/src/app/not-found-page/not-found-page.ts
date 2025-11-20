import { Component, inject } from '@angular/core';
import { AuthService } from '../auth/services/auth.service';
import { PrimaryButton } from '../shared/ui/primary-button/primary-button';

@Component({
  selector: 'app-not-found-page',
  imports: [PrimaryButton],
  templateUrl: './not-found-page.html',
  styleUrl: './not-found-page.css',
})
export class NotFoundPage {
  authService: AuthService = inject(AuthService);
}
