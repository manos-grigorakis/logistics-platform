import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Input } from '@angular/core';
import { NgIcon } from '@ng-icons/core';

@Component({
  selector: 'app-dropdown-menu-item',
  imports: [RouterLink, NgIcon],
  templateUrl: './dropdown-menu-item.html',
  styleUrl: './dropdown-menu-item.css',
})
export class DropdownMenuItem {
  @Input() route: string | null = null;
  @Input() icon?: string;
  @Input() iconSize: string = '22';
  @Input() label?: string;
}
