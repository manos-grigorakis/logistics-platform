import { inject, Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { toast } from 'ngx-sonner';
import { Observable, take } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LanguageService {
  private translate = inject(TranslateService);

  constructor() {}

  init(): Observable<unknown> {
    const lang = localStorage.getItem('lang') || 'en';

    this.translate.setFallbackLang('en');
    return this.translate.use(lang);
  }

  setLanguage(lang: string): void {
    this.translate.use(lang);
    localStorage.setItem('lang', lang);
  }

  getCurrentLanguage(): string {
    return this.translate.getCurrentLang() || 'en';
  }

  /**
   * Creates a toast error with the provided key to be translated
   * @param key The key to be used for translation
   * @param params (Optional) Any additional params for translation
   */
  toastError(key: string, params?: object): void {
    this.translate
      .stream(key, params)
      .pipe(take(1))
      .subscribe((msg) => toast.error(msg));
  }

  /**
   * Creates a success toast with the provided key to be translated
   * @param key The key to be used for translation
   * @param params (Optional) Any additional params for translation
   */
  toastSuccess(key: string, params?: object): void {
    this.translate
      .stream(key, params)
      .pipe(take(1))
      .subscribe((msg) => toast.success(msg));
  }

  /**
   * Translate a key based on the current language
   * @param key The key to be used for translation
   * @param params (Optional) Any additional params for translation
   * @returns The translated output based on the current language
   */
  translateKey(key: string, params?: object): string {
    return this.translate.instant(key, params);
  }
}
