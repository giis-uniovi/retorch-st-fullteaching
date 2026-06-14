import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable()
export class FilesEditionService {

  modeEditAnnounced$: Subject<boolean>;
  fileGroupDeletedAnnounced$: Subject<number>;
  fileFilegroupUpdatedAnnounced$: Subject<any>;
  currentModeEdit = false;

  constructor() {
    this.modeEditAnnounced$ = new Subject<boolean>();
    this.fileGroupDeletedAnnounced$ = new Subject<number>();
    this.fileFilegroupUpdatedAnnounced$ = new Subject<any>();
  }

  announceModeEdit(objs: boolean) {
    this.currentModeEdit = objs;
    this.modeEditAnnounced$.next(objs);
  }
  announceFileGroupDeleted(fileGroupDeletedId: number) { this.fileGroupDeletedAnnounced$.next(fileGroupDeletedId); }
  announceFileFilegroupUpdated(objs: any) { this.fileFilegroupUpdatedAnnounced$.next(objs); }
}
