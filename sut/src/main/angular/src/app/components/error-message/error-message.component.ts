import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';

@Component({
    selector: 'app-error-message',
    templateUrl: './error-message.component.html',
    styleUrls: ['./error-message.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class ErrorMessageComponent {

  @Input()
  errorTitle: string;
  @Input()
  errorContent: string;
  @Input()
  customClass: string;
  @Input()
  closable: boolean;
  @Input()
  timeable: number;

  @Output()
  eventShowable = new EventEmitter<boolean>();

  constructor() { }

  public closeAlert() {
    this.eventShowable.emit(false);
  }

}
