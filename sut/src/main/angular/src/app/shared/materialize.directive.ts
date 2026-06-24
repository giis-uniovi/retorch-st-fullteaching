import { Directive, Input, ElementRef, AfterViewInit, OnDestroy, EventEmitter, NgZone } from '@angular/core';

export type MaterializeAction = { action: string; params: any[] };

declare const $: any;

@Directive({
    selector: '[materialize]',
    standalone: false
})
export class MaterializeDirective implements AfterViewInit, OnDestroy {
  @Input() materialize: string;
  @Input() materializeParams: any[] = [{}];

  @Input() set materializeActions(emitter: EventEmitter<string | MaterializeAction>) {
    if (!emitter) { return; }
    emitter.subscribe((action: string | MaterializeAction) => {
      this.zone.runOutsideAngular(() => {
        if (typeof action === 'string') {
          $(this.el.nativeElement)[action]();
        } else if (action?.action) {
          $(this.el.nativeElement)[action.action](...(action.params || []));
        }
      });
    });
  }

  private hrefEventKey: string | null = null;

  constructor(private readonly el: ElementRef, private readonly zone: NgZone) {}

  ngAfterViewInit() {
    if (!this.materialize) { return; }

    this.zone.runOutsideAngular(() => {
      $(this.el.nativeElement)[this.materialize](this.materializeParams || [{}]);

      // For modals: intercept any <a href="#id"> clicks so the modal opens
      // without requiring the Materialize .modal-trigger class on every anchor.
      if (this.materialize === 'modal') {
        const id = (this.el.nativeElement as HTMLElement).id;
        if (id) {
          this.hrefEventKey = `click.materialize-modal-${id}`;
          $(document).on(this.hrefEventKey, `[href="#${id}"]`, (e: Event) => {
            e.preventDefault();
            $(this.el.nativeElement).modal('open');
          });
        }
      }
    });
  }

  ngOnDestroy() {
    if (this.hrefEventKey) {
      $(document).off(this.hrefEventKey);
    }
  }
}
