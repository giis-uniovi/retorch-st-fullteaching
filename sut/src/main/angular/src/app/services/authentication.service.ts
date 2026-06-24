import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { User } from '../classes/user';

@Injectable()
export class AuthenticationService {

  private readonly urlLogIn = '/api-logIn';
  private readonly urlLogOut = '/api-logOut';

  public token: string;
  private user: User;
  private role: string;

  constructor(private readonly http: HttpClient, private readonly router: Router) {}

  logIn(user: string, pass: string) {
    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + utf8_to_b64(user + ':' + pass),
      'X-Requested-With': 'XMLHttpRequest'
    });
    return this.http.get<User>(this.urlLogIn, { headers }).pipe(
      map(response => { this.processLogInResponse(response); return this.user; }),
      catchError(error => throwError(() => error))
    );
  }

  logOut() {
    console.log('Logging out...');
    return this.http.get(this.urlLogOut).pipe(
      map(response => {
        console.log('Logout succesful!');
        this.user = null;
        this.role = null;
        this.token = null;
        localStorage.removeItem('login');
        localStorage.removeItem('rol');
        this.router.navigate(['']);
        return response;
      }),
      catchError(error => throwError(() => error))
    );
  }

  private processLogInResponse(response: User) {
    console.log('User is already logged');
    this.user = response;
    localStorage.setItem('login', 'FULLTEACHING');
    if (this.user.roles.indexOf('ROLE_ADMIN') !== -1) {
      this.role = 'ROLE_ADMIN';
      localStorage.setItem('rol', 'ROLE_ADMIN');
    }
    if (this.user.roles.indexOf('ROLE_TEACHER') !== -1) {
      this.role = 'ROLE_TEACHER';
      localStorage.setItem('rol', 'ROLE_TEACHER');
    }
    if (this.user.roles.indexOf('ROLE_STUDENT') !== -1) {
      this.role = 'ROLE_STUDENT';
      localStorage.setItem('rol', 'ROLE_STUDENT');
    }
  }

  reqIsLogged(): Promise<any> {
    return new Promise((resolve, reject) => {
      console.log('Checking if user is logged');
      const headers = new HttpHeaders({ 'X-Requested-With': 'XMLHttpRequest' });
      this.http.get<User>(this.urlLogIn, { headers }).subscribe({
        next: response => { this.processLogInResponse(response); resolve(null); },
        error: error => {
          let msg = '';
          if (error.status === 401) {
            msg = 'User is not logged in';
            console.warn(msg);
            this.router.navigate(['']);
          } else {
            msg = 'Error when asking if logged: ' + JSON.stringify(error);
            console.error(msg);
            this.logOut();
          }
          reject(new Error(msg));
        }
      });
    });
  }

  checkCredentials(): Promise<any> {
    return new Promise((resolve, reject) => {
      if (this.isLoggedIn()) {
        resolve(null);
      } else {
        this.reqIsLogged().then(() => resolve(null)).catch(error => reject(error));
      }
    });
  }

  isLoggedIn() { return !!this.user; }
  getCurrentUser() { return this.user; }
  isTeacher() {
    return (this.user.roles.indexOf('ROLE_TEACHER') !== -1) && (localStorage.getItem('rol') === 'ROLE_TEACHER');
  }
  isStudent() {
    return (this.user.roles.indexOf('ROLE_STUDENT') !== -1) && (localStorage.getItem('rol') === 'ROLE_STUDENT');
  }
  isAdmin() {
    return (this.user.roles.indexOf('ROLE_ADMIN') !== -1) && (localStorage.getItem('rol') === 'ROLE_ADMIN');
  }
}

function utf8_to_b64(str: string): string {
  return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, (_m, p1) =>
    String.fromCodePoint(Number.parseInt('0x' + p1, 16))
  ));
}
