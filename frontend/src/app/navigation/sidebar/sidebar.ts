import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SidebarMenuItem } from '../sidebar-menu-item/sidebar-menu-item';

@Component({
  selector: 'app-sidebar',
  imports: [SidebarMenuItem],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  @Input() isOpen?: boolean;
  @Input() isAdmin?: boolean;
  @Input() disableSidebarTransition: boolean = false;
  @Output() onCloseOnMobile = new EventEmitter<void>();

  public closeOnMobile(): void {
    this.onCloseOnMobile.emit();
  }
}
