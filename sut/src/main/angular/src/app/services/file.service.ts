import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import * as FileSaver from 'file-saver';

import { FileGroup } from '../classes/file-group';
import { File } from '../classes/file';
import { CourseDetails } from '../classes/course-details';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class FileService {

  private readonly url = '/api-files';
  private pendingDownload = false;

  constructor(private readonly http: HttpClient, private readonly authenticationService: AuthenticationService) {}

  private authHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + this.authenticationService.token
    });
  }

  newFileGroup(fileGroup: FileGroup, courseDetailsId: number) {
    console.log('POST new filegroup');
    return this.http.post<CourseDetails>(this.url + '/' + courseDetailsId, fileGroup, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('POST new filegroup SUCCESS', r); return r; }),
      catchError(e => this.handleError('POST new filegroup FAIL', e))
    );
  }

  deleteFileGroup(fileGroupId: number, courseId: number) {
    console.log('DELETE filegroup ' + fileGroupId);
    return this.http.delete<FileGroup>(this.url + '/delete/file-group/' + fileGroupId + '/course/' + courseId, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('DELETE filegroup SUCCESS'); return r; }),
      catchError(e => this.handleError('DELETE filegroup FAIL', e))
    );
  }

  deleteFile(fileId: number, fileGroupId: number, courseId: number) {
    console.log('DELETE file ' + fileId);
    return this.http.delete<File>(this.url + '/delete/file/' + fileId + '/file-group/' + fileGroupId + '/course/' + courseId, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('DELETE file SUCCESS'); return r; }),
      catchError(e => this.handleError('DELETE file FAIL', e))
    );
  }

  editFileGroup(fileGroup: FileGroup, courseId: number) {
    console.log('PUT existing filegroup ' + fileGroup.id);
    return this.http.put<FileGroup>(this.url + '/edit/file-group/course/' + courseId, fileGroup, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('PUT existing filegroup SUCCESS', r); return r; }),
      catchError(e => this.handleError('PUT existing filegroup FAIL', e))
    );
  }

  editFileOrder(fileMovedId: number, fileGroupSourceId: number, fileGroupTargetId: number, filePosition: number, courseId: number) {
    console.log('PUT existing filegroups (editing file order)');
    const url = this.url + '/edit/file-order/course/' + courseId + '/file/' + fileMovedId +
                '/from/' + fileGroupSourceId + '/to/' + fileGroupTargetId + '/pos/' + filePosition;
    return this.http.put<FileGroup[]>(url, null, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('PUT existing filegroups SUCCESS (edit file order)', r); return r; }),
      catchError(e => this.handleError('PUT existing filegroups FAIL (edit file order)', e))
    );
  }

  editFile(file: File, fileGroupId: number, courseId: number) {
    console.log('PUT existing file ' + file.name);
    return this.http.put<FileGroup>(this.url + '/edit/file/file-group/' + fileGroupId + '/course/' + courseId, file, { headers: this.authHeaders() }).pipe(
      map(r => { console.log('PUT existing file SUCCESS', r); return r; }),
      catchError(e => this.handleError('PUT existing file FAIL', e))
    );
  }

  downloadFile(courseId: number, file: File) {
    console.log('Downloading file ' + file.name);
    this.pendingDownload = true;
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/api-load-files/course/' + courseId + '/download/' + file.id, true);
    xhr.responseType = 'blob';
    xhr.onreadystatechange = () => {
      if (xhr.readyState === 4) {
        this.pendingDownload = false;
        if (xhr.status === 200) {
          console.log('File download SUCCESS');
          const blob = new Blob([xhr.response], { type: xhr.response.type });
          FileSaver.saveAs(blob, file.name);
        }
      }
    };
    xhr.send();
  }

  private handleError(message: string, error: any): Observable<never> {
    console.error(message, error);
    return throwError(() => `Server error (${error.status}): ${error.statusText}`);
  }
}
