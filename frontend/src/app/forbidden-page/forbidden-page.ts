import { Component, inject } from '@angular/core';
import { PrimaryButton } from '../shared/ui/primary-button/primary-button';
import { AuthService } from '../auth/services/auth.service';

@Component({
  selector: 'app-forbidden-page',
  imports: [PrimaryButton],
  templateUrl: './forbidden-page.html',
  styleUrl: './forbidden-page.css',
})
export class ForbiddenPage {
  public authService: AuthService = inject(AuthService);
}
