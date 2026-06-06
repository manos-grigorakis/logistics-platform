import { Component, inject, Input, OnInit } from '@angular/core';
import { LanguageService } from '../../../core/services/language.service';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-language-switcher',
  imports: [NgClass],
  templateUrl: './language-switcher.html',
  styleUrl: './language-switcher.css',
})
export class LanguageSwitcher implements OnInit {
  @Input() variant: 'light' | 'dark' = 'light';

  public currentLanguage: string = 'en';
  private languageService = inject(LanguageService);

  ngOnInit(): void {
    this.currentLanguage = this.languageService.getCurrentLanguage();
  }

  public getTextClass(lang: string): string {
    const isActive = this.currentLanguage === lang;

    if (this.variant === 'dark') {
      return isActive ? 'text-white' : 'text-white/40';
    }
    return isActive ? 'text-gray-900' : 'text-gray-400';
  }

  public changeLanguage(lang: string): void {
    this.currentLanguage = lang;
    this.languageService.setLanguage(lang);
  }
}
