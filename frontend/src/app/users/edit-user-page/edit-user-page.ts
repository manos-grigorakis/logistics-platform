import { Component, inject, OnInit } from '@angular/core';
import { UsersService } from '../users.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserResponse } from '../models/user-response';
import { UserRequest } from '../models/user-request';
import { UserForm } from '../user-form/user-form';
import { TranslatePipe } from '@ngx-translate/core';
import { LanguageService } from '../../shared/services/language.service';

@Component({
  selector: 'app-edit-user',
  imports: [UserForm, TranslatePipe],
  templateUrl: './edit-user-page.html',
  styleUrl: './edit-user-page.css',
})
export class EditUserPage implements OnInit {
  private userService: UsersService = inject(UsersService);
  private languageService = inject(LanguageService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);
  private id: number = 0;
  public user?: UserResponse;

  public errorMessage?: string;
  public isLoading: boolean = false;

  ngOnInit(): void {
    let tempId = this.route.snapshot.paramMap.get('id');

    if (!tempId) return;

    this.id = parseInt(tempId);
    this.fetchUserData(this.id);
  }

  public onSubmit(data: UserRequest): void {
    this.errorMessage = undefined;
    this.isLoading = true;

    this.userService.updateUser(this.id, data).subscribe({
      next: () => {
        this.isLoading = false;
        this.languageService.toastSuccess('users.messages.success-update');
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

  private fetchUserData(id: number): void {
    this.userService.getUser(id).subscribe({
      next: (res) => {
        this.user = res.data;
        this.errorMessage = undefined;
      },
      error: (err) => {
        if (err.status === 404) {
          this.errorMessage = 'users.messages.not-found';
        } else if (err.status === 500) {
          this.errorMessage = 'common.errors.server';
        } else {
          this.errorMessage = 'common.errors.generic';
        }
      },
    });
  }
}
