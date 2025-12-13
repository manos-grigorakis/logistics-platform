import { Component, HostListener, inject, OnInit } from '@angular/core';
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
export class MainLayout implements OnInit {
  public isSidebarOpen: boolean = true;
  public disableSidebarTransition: boolean = false;
  public authService: AuthService = inject(AuthService);

  private readonly localStorageSidebarState: string = 'localStorageSidebarState';
  private readonly mobileBreakpoint: number = 640;

  ngOnInit(): void {
    this.disableSidebarTransition = true;
    const currentState = localStorage.getItem(this.localStorageSidebarState);

    if (window.innerWidth < this.mobileBreakpoint) {
      this.isSidebarOpen = false;
    } else {
      this.isSidebarOpen = currentState ? currentState === 'true' : true;
    }

    requestAnimationFrame(() => {
      this.disableSidebarTransition = false;
    });
  }

  public toggleSidebar(): void {
    this.disableSidebarTransition = false;
    this.setSidebarOpen(!this.isSidebarOpen);
  }

  public closeOnMobile(): void {
    if (window.innerWidth < this.mobileBreakpoint) {
      this.disableSidebarTransition = false;
      this.isSidebarOpen = false;
    }
  }

  @HostListener('window:resize')
  public onResize(): void {
    this.disableSidebarTransition = true;

    // Change without animation
    requestAnimationFrame(() => {
      if (window.innerWidth < this.mobileBreakpoint) {
        this.isSidebarOpen = false;
      } else {
        const currentState = localStorage.getItem(this.localStorageSidebarState);
        this.isSidebarOpen = currentState ? currentState === 'true' : true;
      }

      // Reset on next frame
      requestAnimationFrame(() => {
        this.disableSidebarTransition = false;
      });
    });
  }

  private setSidebarOpen(state: boolean): void {
    this.isSidebarOpen = state;

    if (window.innerWidth >= this.mobileBreakpoint) {
      localStorage.setItem(this.localStorageSidebarState, String(state));
    }
  }
}
