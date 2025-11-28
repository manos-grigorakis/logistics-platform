import { Component, Input } from '@angular/core';
import { SidebarMenuItem } from '../sidebar-menu-item/sidebar-menu-item';

@Component({
  selector: 'app-sidebar',
  imports: [SidebarMenuItem],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  @Input() isOpen?: boolean;
}
