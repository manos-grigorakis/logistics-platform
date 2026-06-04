import { Component, inject } from '@angular/core';
import { UsersService } from '../users.service';
import { UserRequest } from '../models/user-request';
import { Router } from '@angular/router';
import { UserForm } from '../user-form/user-form';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../shared/services/language.service';

@Component({
  selector: 'app-create-user-page',
  imports: [UserForm, TranslatePipe],
  templateUrl: './create-user-page.html',
  styleUrl: './create-user-page.css',
})
export class CreateUserPage {
  private userService: UsersService = inject(UsersService);
  private languageService = inject(LanguageService);
  private router = inject(Router);
  public isLoading: boolean = false;
  public errorMessage?: string;

  public onSubmit(data: UserRequest): void {
    this.errorMessage = undefined;
    this.isLoading = true;

    this.userService.createUser(data).subscribe({
      next: () => {
        this.isLoading = false;
        this.languageService.toastSuccess('users.messages.success-creation');
        this.router.navigate(['/users']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 409) {
          this.errorMessage = this.languageService.translateKey('users.messages.email-exists', {
            email: data.email,
          });
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }
}
