import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Session } from '../classes/session';
import { Course } from '../classes/course';

@Injectable()
export class VideoSessionService {

  session: Session;
  course: Course;

  private readonly urlSessions = '/api-video-sessions';

  constructor(private readonly http: HttpClient) {}

  getSessionIdAndToken(mySessionId: number) {
    console.log('Getting OpenVidu sessionId and token for lesson \'' + mySessionId + '\'');
    return this.http.get<any>(this.urlSessions + '/get-sessionid-token/' + mySessionId).pipe(
      map(r => { console.log('OpenVidu sessionId and token retrieval SUCCESS', r); return r; }),
      catchError(e => this.handleError('ERROR getting OpenVidu sessionId and token', e))
    );
  }

  removeUser(sessionName: any) {
    console.log('Removing user from session ' + sessionName);
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(this.urlSessions + '/remove-user', { lessonId: sessionName }, { headers, responseType: 'text' }).pipe(
      map(r => { console.log('User removed from session succesfully', r); return r; }),
      catchError(e => this.handleError('ERROR removing user from session', e))
    );
  }

  private handleError(message: string, error: any): Observable<never> {
    console.error(message, error);
    return throwError(() => `Server error (${error.status}): ${error.statusText}`);
  }
}
