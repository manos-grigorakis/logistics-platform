import { Component, EventEmitter, HostListener, inject, OnInit, Output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';
import { DropdownMenuItem } from '../dropdown-menu-item/dropdown-menu-item';
import { User } from '../../auth/models/User';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, DropdownMenuItem],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  private router = inject(Router);
  private authService: AuthService = inject(AuthService);
  @Output() toggleSidebar = new EventEmitter<void>();

  user?: User;
  isUserDropdownOpen: boolean = false;
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

  // Hide dropdown if user clicks somewhere
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    this.isUserDropdownOpen = false;
  }

  public toggleUserDropdown(event: MouseEvent): void {
    event.stopPropagation();
    this.isUserDropdownOpen = !this.isUserDropdownOpen;
  }

  private formatUserName(): string | undefined {
    if (!this.user) return;

    const firstCharLastName = Array.from(this.user.lastName)[0];

    return `${this.user.firstName} ${firstCharLastName}.`;
  }
}
