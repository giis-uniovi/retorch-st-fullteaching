import { Component, EventEmitter } from '@angular/core';
import { TestBed, ComponentFixture } from '@angular/core/testing';

import { NativeDialogDirective, DialogAction } from '../shared/native-dialog.directive';

@Component({
  template: `
    <a id="opener" href="#test-dialog">Open</a>
    <dialog id="test-dialog" class="modal" appModalDialog closedby="none" [dialogActions]="actions">
      <button type="button" id="closer" class="modal-close">Close</button>
    </dialog>`,
  standalone: false,
})
class HostComponent {
  readonly actions = new EventEmitter<string | DialogAction>();
}

describe('NativeDialogDirective', () => {
  let fixture: ComponentFixture<HostComponent>;
  let dialog: HTMLDialogElement;

  beforeEach(() => {
    TestBed.configureTestingModule({ declarations: [HostComponent, NativeDialogDirective] });
    fixture = TestBed.createComponent(HostComponent);
    document.body.appendChild(fixture.nativeElement);
    fixture.detectChanges();
    dialog = fixture.nativeElement.querySelector('#test-dialog');
  });

  afterEach(() => {
    fixture.destroy();
  });

  it('opens as a modal and closes through the actions emitter', () => {
    fixture.componentInstance.actions.emit({ action: 'modal', params: ['open'] });
    expect(dialog.open).toBeTrue();
    expect(dialog.matches(':modal')).toBeTrue();
    fixture.componentInstance.actions.emit({ action: 'modal', params: ['close'] });
    expect(dialog.open).toBeFalse();
  });

  it('opens when an <a href="#id"> is clicked and closes with .modal-close', () => {
    fixture.nativeElement.querySelector('#opener').click();
    expect(dialog.open).toBeTrue();
    fixture.nativeElement.querySelector('#closer').click();
    expect(dialog.open).toBeFalse();
  });

  it('cannot be dismissed with the cancel request', () => {
    fixture.componentInstance.actions.emit({ action: 'modal', params: ['open'] });
    const cancel = new Event('cancel', { cancelable: true });
    dialog.dispatchEvent(cancel);
    expect(cancel.defaultPrevented).toBeTrue();
    expect(dialog.open).toBeTrue();
  });
});
