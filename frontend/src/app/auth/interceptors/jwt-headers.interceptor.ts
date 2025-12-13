import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { SKIP_AUTH } from './auth-http.context';
import { Router } from '@angular/router';

@Injectable()
export class JwtHeadersInterceptor implements HttpInterceptor {
  private authService: AuthService = inject(AuthService);
  private router: Router = inject(Router);

  public intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.context.get(SKIP_AUTH)) {
      return next.handle(req);
    }

    const jwtToken = this.authService.getJwtToken();
    let authReq = req;

    if (jwtToken) {
      authReq = req.clone({
        setHeaders: { Authorization: `Bearer ${jwtToken}` },
      });
    }

    return next.handle(authReq).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }

        if (err.status === 403) {
          this.router.navigate(['/forbidden']);
        }

        return throwError(() => err);
      }),
    );
  }
}
