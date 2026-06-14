export class QueueItem {
  file: { name: string };
  isReady = true;
  isUploading = false;
  isSuccess = false;
  isError = false;
  isCancel = false;

  private xhr: XMLHttpRequest | null = null;
  private readonly uploader: NativeFileUploader;
  private readonly rawFile: File;

  constructor(rawFile: File, uploader: NativeFileUploader) {
    this.rawFile = rawFile;
    this.file = { name: rawFile.name };
    this.uploader = uploader;
  }

  upload() {
    const xhr = new XMLHttpRequest();
    this.xhr = xhr;
    this.isUploading = true;
    this.isReady = false;
    this.uploader.isUploading = true;
    this.uploader.progress = 0;
    this.uploader._notifyUploadStart();

    xhr.upload.onprogress = (e) => {
      if (e.lengthComputable) {
        this.uploader.progress = Math.round((e.loaded / e.total) * 100);
      }
    };

    xhr.onreadystatechange = () => {
      if (xhr.readyState !== 4) { return; }
      this.isUploading = false;
      this.uploader.isUploading = false;
      if (xhr.status >= 200 && xhr.status < 300) {
        this.isSuccess = true;
        this.uploader.progress = 100;
        this.uploader._notifyComplete(xhr.responseText);
      } else {
        this.isError = true;
      }
    };

    const fd = new FormData();
    fd.append('file', this.rawFile, this.rawFile.name);
    xhr.open('POST', this.uploader.url, true);
    xhr.send(fd);
  }

  cancel() {
    if (this.xhr) { this.xhr.abort(); }
    this.isUploading = false;
    this.isCancel = true;
  }

  remove() {
    this.uploader.queue = this.uploader.queue.filter(i => i !== this);
  }
}

export class NativeFileUploader {
  url: string;
  maxFileSize: number;
  queue: QueueItem[] = [];
  isUploading = false;
  progress = 0;

  onBeforeUploadItem: (_item: QueueItem) => void = () => {};
  onCompleteItem: (_item: QueueItem, response: string, status: number, _headers: any) => void = () => {};
  onWhenAddingFileFailed: (_item: File, _filter: any, _options: any) => void = () => {};
  onCancelItem: (_item: QueueItem, _response: string, _status: number, _headers: any) => void = () => {};

  constructor(options: { url: string; maxFileSize?: number }) {
    this.url = options.url;
    this.maxFileSize = options.maxFileSize ?? Infinity;
  }

  addFiles(files: FileList | File[]) {
    Array.from(files).forEach(f => {
      if (f.size > this.maxFileSize) {
        this.onWhenAddingFileFailed(f, null, null);
      } else {
        this.queue.push(new QueueItem(f, this));
      }
    });
  }

  uploadAll() {
    this.getNotUploadedItems().forEach(i => i.upload());
  }

  cancelAll() { this.queue.forEach(i => i.cancel()); }

  clearQueue() { this.queue = []; }

  getNotUploadedItems() {
    return this.queue.filter(i => !i.isSuccess && !i.isUploading);
  }

  destroy() { this.clearQueue(); }

  _notifyUploadStart() { this.onBeforeUploadItem(this.queue[this.queue.length - 1]); }

  _notifyComplete(response: string) {
    const item = this.queue.find(i => i.isSuccess) ?? this.queue[0];
    this.onCompleteItem(item, response, 200, {});
  }
}
