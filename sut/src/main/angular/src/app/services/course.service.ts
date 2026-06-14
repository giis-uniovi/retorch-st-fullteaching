import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Course } from '../classes/course';
import { User } from '../classes/user';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class CourseService {

  private readonly url = '/api-courses';

  constructor(private readonly http: HttpClient, private readonly authenticationService: AuthenticationService) {}

  private authHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + this.authenticationService.token
    });
  }

  getCourses(user: User) {
    console.log('GET courses for user ' + user.nickName);
    return this.http.get<Course[]>(this.url + '/user/' + user.id, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('GET courses SUCCESS', r); return r; }),
      catchError(e => this.handleError('GET courses FAIL', e))
    );
  }

  getCourse(courseId: number) {
    console.log('GET course ' + courseId);
    return this.http.get<Course>(this.url + '/course/' + courseId, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('GET course SUCCESS', r); return r; }),
      catchError(e => this.handleError('GET course FAIL', e))
    );
  }

  newCourse(course: Course) {
    console.log('POST new course');
    const headers = new HttpHeaders({ 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' });
    return this.http.post<Course>(this.url + '/new', course, { headers }).pipe(
      map(r => { console.log('POST new course SUCCESS', r); return r; }),
      catchError(e => this.handleError('POST new course FAIL', e))
    );
  }

  editCourse(course: Course, context: string) {
    console.log('PUT existing course ' + course.id + ' (' + context + ')');
    return this.http.put<Course>(this.url + '/edit', course, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('PUT existing course SUCCESS (' + context + ')', r); return r; }),
      catchError(e => this.handleError('PUT existing course FAIL (' + context + ')', e))
    );
  }

  deleteCourse(courseId: number) {
    console.log('DELETE course ' + courseId);
    return this.http.delete<Course>(this.url + '/delete/' + courseId, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('DELETE course SUCCESS'); return r; }),
      catchError(e => this.handleError('DELETE course FAIL', e))
    );
  }

  addCourseAttenders(courseId: number, userEmails: string[]) {
    console.log('PUT existing course (add attenders)');
    return this.http.put<any>(this.url + '/edit/add-attenders/course/' + courseId, userEmails, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('PUT course SUCCESS (add attenders)', r); return r; }),
      catchError(e => this.handleError('PUT course FAIL (add attenders)', e))
    );
  }

  deleteCourseAttenders(course: Course) {
    console.log('PUT existing course (remove attender)');
    return this.http.put<User[]>(this.url + '/edit/delete-attenders', course, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('PUT course SUCCESS (remove attender)', r); return r; }),
      catchError(e => this.handleError('PUT course FAIL (remove attender)', e))
    );
  }

  private handleError(message: string, error: any): Observable<never> {
    console.error(message, error);
    return throwError(() => `Server error (${error.status}): ${error.statusText}`);
  }
}
