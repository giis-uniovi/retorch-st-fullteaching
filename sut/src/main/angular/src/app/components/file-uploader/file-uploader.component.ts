import { Component, OnInit, OnChanges, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { Subscription } from 'rxjs';

import { Constants } from '../../constants';
import { NativeFileUploader } from '../../shared/native-uploader';
import { UploaderModalService } from '../../services/uploader-modal.service';

declare const M: any;

@Component({
    selector: 'app-file-uploader',
    templateUrl: './file-uploader.component.html',
    styleUrls: ['./file-uploader.component.css'],
    standalone: false
})
export class FileUploaderComponent implements OnInit, OnChanges, OnDestroy {

  uploader: NativeFileUploader;
  hasBaseDropZoneOver = false;
  fileIncorrect = false;

  private readonly subscription: Subscription;

  @Input() uniqueID: number;
  @Input() isMultiple: boolean;
  @Input() URLUPLOAD = '/test';
  @Input() typeOfFile: string;
  @Input() buttonText: string;

  @Output() completeFileUpload = new EventEmitter<any>();
  @Output() uploadStarted = new EventEmitter<boolean>();

  constructor(private readonly uploaderModalService: UploaderModalService) {
    this.subscription = this.uploaderModalService.uploaderClosedAnnounced$.subscribe(
      () => { this.uploader?.clearQueue(); this.fileIncorrect = false; }
    );
  }

  ngOnInit() { this.initUploader(); }

  ngOnChanges() {
    if (this.uploader) { this.uploader.destroy(); }
    this.initUploader();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
    this.uploader?.destroy();
  }

  private initUploader() {
    this.uploader = new NativeFileUploader({ url: this.URLUPLOAD, maxFileSize: Constants.FILE_SIZE_LIMIT });
    this.uploader.onBeforeUploadItem = () => { this.uploadStarted.emit(true); };
    this.uploader.onCompleteItem = (_item, response) => { this.completeFileUpload.emit(response); };
    this.uploader.onWhenAddingFileFailed = () => { this.handleFileSizeError(); };
    this.uploader.onCancelItem = () => { console.log('File upload canceled'); };
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) { this.uploader.addFiles(input.files); }
  }

  fileOverBase(over: boolean) { this.hasBaseDropZoneOver = over; }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.hasBaseDropZoneOver = true;
  }

  onDragLeave() { this.hasBaseDropZoneOver = false; }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.hasBaseDropZoneOver = false;
    if (event.dataTransfer?.files) { this.uploader.addFiles(event.dataTransfer.files); }
  }

  handleFileSizeError() {
    console.error('File too big. ' + this.URLUPLOAD);
    if (window.innerWidth <= Constants.PHONE_MAX_WIDTH) {
      M.toast({ html: 'Files cannot be bigger than 5MB!', classes: 'rounded' });
    } else {
      this.fileIncorrect = true;
    }
  }
}
