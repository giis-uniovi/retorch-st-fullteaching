import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Entry } from '../classes/entry';
import { Comment } from '../classes/comment';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class ForumService {

  private readonly urlNewEntry = '/api-entries';
  private readonly urlNewComment = '/api-comments';
  private readonly urlEditForum = '/api-forum';

  constructor(private readonly http: HttpClient, private readonly authenticationService: AuthenticationService) {}

  private authHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + this.authenticationService.token
    });
  }

  newEntry(entry: Entry, courseDetailsId: number) {
    console.log('POST new entry');
    return this.http.post<any>(this.urlNewEntry + '/forum/' + courseDetailsId, entry, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('POST new entry SUCCESS', r); return r; }),
      catchError(e => this.handleError('POST new entry FAIL', e))
    );
  }

  newComment(comment: Comment, entryId: number, courseDetailsId: number) {
    console.log('POST new comment');
    return this.http.post<any>(this.urlNewComment + '/entry/' + entryId + '/forum/' + courseDetailsId, comment, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('POST new comment SUCCESS', r); return r; }),
      catchError(e => this.handleError('POST new comment FAIL', e))
    );
  }

  editForum(activated: boolean, courseDetailsId: number) {
    console.log('PUT existing forum ' + (activated ? '(activate)' : '(deactivate)'));
    return this.http.put<boolean>(this.urlEditForum + '/edit/' + courseDetailsId, activated, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('PUT existing forum SUCCESS', r); return r; }),
      catchError(e => this.handleError('PUT existing forum FAIL', e))
    );
  }

  private handleError(message: string, error: any): Observable<never> {
    console.error(message, error);
    return throwError(() => `Server error (${error.status}): ${error.statusText}`);
  }
}
