import { Component, ElementRef, HostListener, inject, Input } from '@angular/core';

@Component({
  selector: 'app-dropdown-button',
  imports: [],
  templateUrl: './dropdown-button.html',
  styleUrl: './dropdown-button.css',
})
export class DropdownButton {
  @Input() label?: string;
  @Input() applyBorderButton: boolean = true;

  isDropdownOpen: boolean = false;
  private eRef: ElementRef = inject(ElementRef);

  public toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  // Listener that get clicks from DOM into events
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement | null;

    // Close dropdown if click is outside the dropdown
    if (target && !this.eRef.nativeElement.contains(target)) {
      this.isDropdownOpen = false;
    }
  }

  // Close the dropdown if user click a menu item
  public onMenuClick(event: MouseEvent): void {
    const target = event.target as HTMLElement | null;

    if (target && target.closest('[dropdown-menu-item]')) {
      this.isDropdownOpen = false;
    }
  }
}
