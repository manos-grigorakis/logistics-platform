import { Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { FormsModule } from '@angular/forms';
import { LanguageService } from '../../services/language.service';
import { Subscription, take } from 'rxjs';

@Component({
  selector: 'app-search-bar',
  imports: [NgIcon, FormsModule],
  templateUrl: './search-bar.html',
  styleUrl: './search-bar.css',
})
export class SearchBar implements OnInit, OnDestroy {
  @Input() placeholder: string = '';
  @Input() value: string = '';
  @Output() valueChange = new EventEmitter<string>();

  private languageService = inject(LanguageService);
  private langChangeSub?: Subscription;

  ngOnInit(): void {
    this.setPlaceholder();
    this.langChangeSub = this.languageService.onLangChange.subscribe(() => this.setPlaceholder());
  }

  ngOnDestroy(): void {
    this.langChangeSub?.unsubscribe();
  }

  public onInputChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.valueChange.emit(value);
  }

  private setPlaceholder(): void {
    // If placeholder has been passed through input
    if (this.placeholder) return;

    this.languageService
      .translateKeyAsync('common.filters.search')
      .pipe(take(1))
      .subscribe((val) => (this.placeholder = val + '...'));
  }
}
