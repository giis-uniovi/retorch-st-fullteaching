import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { User } from '../classes/user';

@Injectable()
export class UserService {

  private readonly url = '/api-users';

  constructor(private readonly http: HttpClient) {}

  newUser(name: string, pass: string, nickName: string, captchaToken: string) {
    console.log('POST new user ' + name);
    const headers = new HttpHeaders({ 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' });
    return this.http.post<User>(this.url + '/new', [name, pass, nickName, captchaToken], { headers }).pipe(
      map(r => { console.log('POST new user SUCCESS', r); return r; }),
      catchError(e => this.handleError('POST new user FAIL', e))
    );
  }

  changePassword(oldPassword: string, newPassword: string) {
    console.log('PUT existing user (change password)');
    const headers = new HttpHeaders({ 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' });
    return this.http.put<boolean>(this.url + '/changePassword', [oldPassword, newPassword], { headers }).pipe(
      map(r => { console.log('PUT existing user SUCCESS (change password)', r); return r; }),
      catchError(e => this.handleError('PUT existing user FAIL (change password)', e))
    );
  }

  private handleError(message: string, error: any): Observable<never> {
    console.error(message, error);
    return throwError(() => `Server error (${error.status}): ${error.statusText}`);
  }
}
