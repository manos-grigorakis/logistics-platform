import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SidebarMenuItem } from '../sidebar-menu-item/sidebar-menu-item';

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, SidebarMenuItem],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  @Input() isOpen?: boolean;
}
