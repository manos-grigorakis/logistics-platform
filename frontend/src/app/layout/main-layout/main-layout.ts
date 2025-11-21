import { Component, inject } from '@angular/core';
import { AuthService } from '../../auth/services/auth.service';
import { Navbar } from '../../navigation/navbar/navbar';
import { Sidebar } from '../../navigation/sidebar/sidebar';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  imports: [Navbar, Sidebar, RouterOutlet],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.css',
})
export class MainLayout {
  authService: AuthService = inject(AuthService);

  isSidebarOpen: boolean = true;

  public toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }
}
