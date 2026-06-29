import { Component, Input } from '@angular/core';
import { LoadingSpinner } from '../../../../shared/ui/loading-spinner/loading-spinner';
import { AuthHeader } from './auth-header/auth-header';
import { ErrorAlert } from '../../../../shared/ui/error-alert/error-alert';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-auth-layout',
  imports: [LoadingSpinner, AuthHeader, ErrorAlert, NgClass],
  templateUrl: './auth-layout.html',
  styleUrl: './auth-layout.css',
})
export class AuthLayout {
  @Input({ required: true }) isLoading!: boolean;
  @Input({ required: true }) header!: string;
  @Input({ required: true }) errorMessage!: string | null;
}
