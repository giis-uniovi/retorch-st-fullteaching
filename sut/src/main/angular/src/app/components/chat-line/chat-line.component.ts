import { Component, Input, ChangeDetectionStrategy } from '@angular/core';

import { Chatline } from '../../classes/chatline';

@Component({
    selector: 'app-chat-line',
    templateUrl: './chat-line.component.html',
    styleUrls: ['./chat-line.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class ChatLineComponent {

  @Input()
  public chatLine: Chatline;

  constructor() { }

}
