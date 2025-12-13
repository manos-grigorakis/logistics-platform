import { Component, EventEmitter, inject, OnInit, Output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';
import { DropdownMenuItem } from '../dropdown-menu-item/dropdown-menu-item';
import { User } from '../../auth/models/User';
import { DropdownButton } from '../../shared/ui/dropdown-button/dropdown-button';
import { NgIcon } from '@ng-icons/core';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, DropdownMenuItem, DropdownButton, NgIcon],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  private router = inject(Router);
  private authService: AuthService = inject(AuthService);
  @Output() toggleSidebar = new EventEmitter<void>();

  user?: User;
  displayName?: string;

  ngOnInit(): void {
    this.user = this.authService.loadUserData();

    if (!this.user) return;

    this.displayName = this.formatUserName();
  }

  public logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  public onBurgerClick() {
    this.toggleSidebar.emit();
  }

  private formatUserName(): string | undefined {
    if (!this.user) return;

    const firstCharLastName = Array.from(this.user.lastName)[0];

    return `${this.user.firstName} ${firstCharLastName}.`;
  }
}
