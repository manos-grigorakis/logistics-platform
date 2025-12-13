import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { SKIP_AUTH } from './auth-http.context';

@Injectable()
export class JwtHeadersInterceptor implements HttpInterceptor {
  private authService: AuthService = inject(AuthService);

  public intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.context.get(SKIP_AUTH)) {
      return next.handle(req);
    }

    const jwtToken = this.authService.getJwtToken();

    if (!jwtToken) return next.handle(req);

    return next.handle(req.clone({ setHeaders: { Authorization: `Bearer ${jwtToken}` } }));
  }
}
