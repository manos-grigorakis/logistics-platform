import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { RouterLink } from '@angular/router';
import { RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar-menu-item',
  imports: [RouterLink, NgIcon, RouterLinkActive],
  templateUrl: './sidebar-menu-item.html',
  styleUrl: './sidebar-menu-item.css',
})
export class SidebarMenuItem {
  @Input() route: string | null = null;
  @Input() icon?: string;
  @Input() label?: string;
  @Input() badgeValue?: string;
  @Output() onItemClick = new EventEmitter<void>();

  public onClick(): void {
    this.onItemClick.emit();
  }
}
