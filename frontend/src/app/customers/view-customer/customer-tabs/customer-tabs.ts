import { Component, EventEmitter, Output } from '@angular/core';
import { RouterOutlet, RouterLinkWithHref, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-customer-tabs',
  imports: [RouterOutlet, RouterLinkWithHref, RouterLinkActive],
  templateUrl: './customer-tabs.html',
  styleUrl: './customer-tabs.css',
})
export class CustomerTabs {
  @Output() onTab = new EventEmitter<string>();

  public onTabClick(tab: string): void {
    this.onTab.emit(tab);
  }
}
