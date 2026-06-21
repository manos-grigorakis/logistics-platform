import { Component, Input } from '@angular/core';
import { NgIcon } from '@ng-icons/core';

@Component({
  selector: 'app-sidebar-dropdown-menu',
  imports: [NgIcon],
  templateUrl: './sidebar-dropdown-menu.html',
  styleUrl: './sidebar-dropdown-menu.css',
})
export class SidebarDropdownMenu {
  @Input() route: string | null = null;
  @Input({ required: true }) icon!: string;
  @Input({ required: true }) label!: string;

  public showDropdown: boolean = false;

  public openDropdown(): void {
    this.showDropdown = !this.showDropdown;
  }
}
