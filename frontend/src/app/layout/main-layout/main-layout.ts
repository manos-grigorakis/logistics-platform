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
  public isSidebarOpen: boolean = true;
  public authService: AuthService = inject(AuthService);

  public toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  public closeOnMobile(): void {
    if (window.innerWidth < 640) {
      this.isSidebarOpen = false;
    }
  }
}
