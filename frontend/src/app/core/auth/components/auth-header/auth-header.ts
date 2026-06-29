import { Component } from '@angular/core';
import { LanguageSwitcher } from '../../../../shared/ui/language-switcher/language-switcher';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-auth-header',
  imports: [LanguageSwitcher, TranslatePipe],
  templateUrl: './auth-header.html',
  styleUrl: './auth-header.css',
})
export class AuthHeader {}
