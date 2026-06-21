import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SidebarMenuItem } from '../sidebar-menu-item/sidebar-menu-item';
import { LanguageSwitcher } from '../../../shared/ui/language-switcher/language-switcher';
import { TranslatePipe } from '@ngx-translate/core';
import { SidebarDropdownMenu } from '../sidebar-dropdown-menu/sidebar-dropdown-menu';

@Component({
  selector: 'app-sidebar',
  imports: [SidebarMenuItem, LanguageSwitcher, TranslatePipe, SidebarDropdownMenu],
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
