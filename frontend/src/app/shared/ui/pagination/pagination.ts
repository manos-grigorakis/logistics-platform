import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-pagination',
  imports: [NgClass],
  templateUrl: './pagination.html',
  styleUrl: './pagination.css',
})
export class Pagination implements OnChanges {
  @Input() totalPages: number = 0;
  @Input() currentPage: number = 0;
  @Input() totalElements: number = 0;
  @Input() pageSize: number = 0;
  @Output() pageChanged = new EventEmitter<number>();

  public allPages: number[] = [];
  private visiblePages: number = 5;

  ngOnChanges(changes: SimpleChanges): void {
    this.buildPages();
  }

  public get startItem(): number {
    if (this.totalElements === 0) return 0;
    return this.currentPage * this.pageSize + 1;
  }

  public get endItem(): number {
    const end = (this.currentPage + 1) * this.pageSize;

    if (end > this.totalElements) {
      return this.totalElements;
    }

    return end;
  }

  // Build the array of visible pages
  private buildPages(): void {
    if (!this.allPages) {
      this.allPages = [];
      return;
    }

    const total = this.totalPages;
    const current = this.currentPage ?? 0;
    const maxVisible = this.visiblePages;

    // Center the current page in the window
    let start = current - Math.floor(maxVisible / 2);
    start = Math.max(0, start);

    // Shift down if near end
    if (start + maxVisible > total) {
      start = Math.max(0, total - maxVisible);
    }

    // Ensure end index wont exceed total number of pages
    const end = Math.min(total, start + maxVisible);

    this.allPages = [];
    for (let i = start; i < end; i++) {
      this.allPages.push(i);
    }
  }

  // Mark current page as active
  public isPageActive(page: number): boolean {
    return page === this.currentPage;
  }

  public goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.pageChanged.emit(page);
  }

  public nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.pageChanged.emit(this.currentPage + 1);
    }
  }

  public prevPage(): void {
    if (this.currentPage > 0) {
      this.pageChanged.emit(this.currentPage - 1);
    }
  }
}
