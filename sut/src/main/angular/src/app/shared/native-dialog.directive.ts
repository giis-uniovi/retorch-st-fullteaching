import { Directive, ElementRef, EventEmitter, Input, NgZone, OnDestroy, AfterViewInit } from '@angular/core';

export type DialogAction = { action: string; params: any[] };

/**
 * Turns a native <dialog> into the replacement of the former Materialize modal.
 *
 * - Opens with showModal() and closes with close() (the browser provides the top layer, the
 *   inert page, the focus handling and the ::backdrop; nothing is emulated manually).
 * - Keeps the old public API: it listens to the same EventEmitter the components already use
 *   (`actions.emit({ action: 'modal', params: ['open' | 'close'] })`).
 * - Keeps the old triggers: any <a href="#dialog-id"> opens the dialog and any `.modal-close`
 *   element inside it closes it.
 * - The dialog is not dismissible by the user (as `dismissible: false` was in Materialize):
 *   markup carries closedby="none" and the `cancel` event is also prevented for browsers
 *   without `closedby` support (Safari), where Esc would otherwise close it.
 */
@Directive({
  selector: 'dialog[appModalDialog]',
  standalone: false
})
export class NativeDialogDirective implements AfterViewInit, OnDestroy {

  private subscription?: { unsubscribe(): void };
  private readonly dialog: HTMLDialogElement;

  private readonly onCancel = (e: Event) => e.preventDefault();
  private readonly onClose = () => this.dialog.classList.remove('open');
  private readonly onDialogClick = (e: Event) => {
    if ((e.target as Element | null)?.closest?.('.modal-close, [command="close"]')) {
      this.close();
    }
  };
  private readonly onDocumentClick = (e: Event) => {
    const id = this.dialog.id;
    if (!id) { return; }
    const trigger = (e.target as Element | null)?.closest?.(`a[href="#${id}"], button[commandfor="${id}"][command="show-modal"]`);
    if (trigger) {
      e.preventDefault();
      this.open();
    }
  };

  @Input() set dialogActions(emitter: EventEmitter<string | DialogAction> | undefined) {
    this.subscription?.unsubscribe();
    if (!emitter) { return; }
    this.subscription = emitter.subscribe((action: string | DialogAction) => {
      const name = typeof action === 'string' ? action : action?.params?.[0];
      if (name === 'open' || name === 'openModal') { this.open(); }
      else if (name === 'close' || name === 'closeModal') { this.close(); }
    });
  }

  constructor(el: ElementRef<HTMLDialogElement>, private readonly zone: NgZone) {
    this.dialog = el.nativeElement;
  }

  ngAfterViewInit() {
    this.zone.runOutsideAngular(() => {
      this.dialog.addEventListener('cancel', this.onCancel);
      this.dialog.addEventListener('close', this.onClose);
      this.dialog.addEventListener('click', this.onDialogClick);
      document.addEventListener('click', this.onDocumentClick);
    });
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
    this.dialog.removeEventListener('cancel', this.onCancel);
    this.dialog.removeEventListener('close', this.onClose);
    this.dialog.removeEventListener('click', this.onDialogClick);
    document.removeEventListener('click', this.onDocumentClick);
    if (this.dialog.open) { this.dialog.close(); }
  }

  open() {
    if (this.dialog.open || !this.dialog.isConnected) { return; }
    this.dialog.showModal();
    // Kept so that selectors written for the former Materialize modal (".modal.open") still match.
    this.dialog.classList.add('open');
  }

  close() {
    if (this.dialog.open) { this.dialog.close(); }
  }
}
