import { Component, Input, ViewChild, ElementRef, SimpleChanges, AfterViewInit, OnChanges, DoCheck } from '@angular/core';
import { Stream } from 'openvidu-browser';

@Component({
    selector: 'stream',
    styleUrls: ['./stream.component.css'],
    template: `
        <div class='participant' [class.participant-small]="this.small">
          @if (this.stream) {
            <div class="name-div"><p class="name-p">{{this.getName()}}</p></div>
          }
          <video #videoElement autoplay="true" [muted]="this.muted" [attr.title]="getVideoNameFromStream()" ></video>
        </div>`,
    standalone: false
})
export class StreamComponent implements AfterViewInit, OnChanges, DoCheck {

  @ViewChild('videoElement') elementRef: ElementRef;
  videoElement: HTMLVideoElement;

  @Input()
  stream: Stream;

  @Input()
  small: boolean;

  @Input()
  muted: boolean;

  constructor() { }

  ngAfterViewInit() {
    this.videoElement = this.elementRef.nativeElement;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['muted']) {
      this.muted = changes['muted'].currentValue;
      console.warn("Small: " + this.small + " | Muted: " + this.muted);
    }
  }

  ngDoCheck() {
    if (this.videoElement?.srcObject !== this.stream.getMediaStream()) {
      if (this.videoElement) {
        this.videoElement.srcObject = this.stream.getMediaStream();
      }
    }
  }

  getName() {
    return ((JSON.parse(this.stream.connection.data))['name']);
  }

  getVideoNameFromStream(): string {
    return this.stream == null ? 'VIDEO' : 'VIDEO-' + this.getName();
  }

}
