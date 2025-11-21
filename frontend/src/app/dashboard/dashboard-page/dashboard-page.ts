import { Component, inject } from '@angular/core';
import { AuthService } from '../../auth/services/auth.service';
import { User } from '../../auth/models/User';
import { PrimaryButton } from '../../shared/ui/primary-button/primary-button';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard-page',
  imports: [PrimaryButton],
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.css',
})
export class DashboardPage {
  private router = inject(Router);
  authService: AuthService = inject(AuthService);

  userData?: User;

  constructor() {
    this.userData = this.authService.loadUserData();
  }

  logout() {
    this.authService.logout();

    this.router.navigate(['/login']);
  }
}
