import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { Page } from '../../models/page.interface';

@Component({
  selector: 'app-pagination',
  imports: [NgClass, TranslatePipe],
  templateUrl: './pagination.html',
  styleUrl: './pagination.css',
})
export class Pagination implements OnChanges {
  @Input({ required: true }) page!: Page;
  @Output() pageChanged = new EventEmitter<number>();

  public allPages: number[] = [];
  private visiblePages: number = 5;

  ngOnChanges(changes: SimpleChanges): void {
    this.buildPages();
  }

  public get startItem(): number {
    if (this.page.totalElements === 0) return 0;
    return this.page.number * this.page.size + 1;
  }

  public get endItem(): number {
    const end = (this.page.number + 1) * this.page.size;

    if (end > this.page.totalElements) {
      return this.page.totalElements;
    }

    return end;
  }

  // Build the array of visible pages
  private buildPages(): void {
    if (!this.allPages) {
      this.allPages = [];
      return;
    }

    const total = this.page.totalPages;
    const current = this.page.number ?? 0;
    const maxVisible = this.visiblePages;

    // Center the current page in the window
    let start = current - Math.floor(maxVisible / 2);
    start = Math.max(0, start);

    // Shift down if near end
    if (start + maxVisible > total) {
      start = Math.max(0, total - maxVisible);
    }

    // Ensure end index won't exceed total number of pages
    const end = Math.min(total, start + maxVisible);

    this.allPages = [];
    for (let i = start; i < end; i++) {
      this.allPages.push(i);
    }
  }

  // Mark current page as active
  public isPageActive(page: number): boolean {
    return page === this.page.number;
  }

  public goToPage(page: number): void {
    if (page < 0 || page >= this.page.totalPages) return;
    this.pageChanged.emit(page);
  }

  public nextPage(): void {
    if (this.page.number < this.page.totalPages - 1) {
      this.pageChanged.emit(this.page.number + 1);
    }
  }

  public prevPage(): void {
    if (this.page.number > 0) {
      this.pageChanged.emit(this.page.number - 1);
    }
  }
}
