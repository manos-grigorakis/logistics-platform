import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { FormsModule } from '@angular/forms';
import { LanguageService } from '../../services/language.service';
import { take } from 'rxjs';

@Component({
  selector: 'app-search-bar',
  imports: [NgIcon, FormsModule],
  templateUrl: './search-bar.html',
  styleUrl: './search-bar.css',
})
export class SearchBar implements OnInit {
  @Input() placeholder: string = '';
  @Input() value: string = '';
  @Output() valueChange = new EventEmitter<string>();

  private languageService = inject(LanguageService);

  ngOnInit(): void {
    if (!this.placeholder) {
      this.languageService
        .translateKeyAsync('common.filters.search')
        .pipe(take(1))
        .subscribe((val) => (this.placeholder = val + '...'));
    }
  }

  public onInputChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.valueChange.emit(value);
  }
}
