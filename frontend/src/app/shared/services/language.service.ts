import { inject, Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root',
})
export class LanguageService {
  private translate = inject(TranslateService);

  constructor() {}

  init(): void {
    const lang = localStorage.getItem('lang') || 'en';

    this.translate.setFallbackLang('en');
    this.translate.use(lang);
  }

  setLanguage(lang: string): void {
    this.translate.use(lang);
    localStorage.setItem('lang', lang);
  }

  getCurrentLanguage(): string {
    return this.translate.getCurrentLang() || 'en';
  }
}
