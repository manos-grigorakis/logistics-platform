import { Component, inject } from '@angular/core';
import { PrimaryButton } from '../shared/ui/primary-button/primary-button';
import { AuthService } from '../auth/services/auth.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-forbidden-page',
  imports: [PrimaryButton, TranslatePipe],
  templateUrl: './forbidden-page.html',
  styleUrl: './forbidden-page.css',
})
export class ForbiddenPage {
  public authService: AuthService = inject(AuthService);
}
