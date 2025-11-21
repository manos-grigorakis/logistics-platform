import { Component, EventEmitter, HostListener, inject, Output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';
import { DropdownMenuItem } from '../dropdown-menu-item/dropdown-menu-item';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, DropdownMenuItem],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  private router = inject(Router);
  authService: AuthService = inject(AuthService);
  @Output() toggleSidebar = new EventEmitter<void>();

  isUserDropdownOpen: boolean = false;

  public logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  public onBurgerClick() {
    this.toggleSidebar.emit();
  }

  // Hide dropdown if user clicks somewhere
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    this.isUserDropdownOpen = false;
  }

  public toggleUserDropdown(event: MouseEvent): void {
    event.stopPropagation();
    this.isUserDropdownOpen = !this.isUserDropdownOpen;
  }
}
