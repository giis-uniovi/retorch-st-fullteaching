import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Session } from '../classes/session';
import { Course } from '../classes/course';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class SessionService {

  private readonly urlSessions = '/api-sessions';

  constructor(private readonly http: HttpClient, private readonly authenticationService: AuthenticationService) {}

  private authHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + this.authenticationService.token
    });
  }

  newSession(session: Session, courseId: number) {
    console.log('POST new session');
    return this.http.post<Course>(this.urlSessions + '/course/' + courseId, session, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('POST new session SUCCESS', r); return r; }),
      catchError(e => this.handleError('POST new session FAIL', e))
    );
  }

  editSession(session: Session) {
    console.log('PUT existing session');
    return this.http.put<Session>(this.urlSessions + '/edit', session, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('PUT existing session SUCCESS', r); return r; }),
      catchError(e => this.handleError('PUT existing session FAIL', e))
    );
  }

  deleteSession(sessionId: number) {
    console.log('DELETE session');
    return this.http.delete<Session>(this.urlSessions + '/delete/' + sessionId, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('DELETE session SUCCESS', r); return r; }),
      catchError(e => this.handleError('DELETE session FAIL', e))
    );
  }

  private handleError(message: string, error: any): Observable<never> {
    console.error(message, error);
    return throwError(() => `Server error (${error.status}): ${error.statusText}`);
  }
}
