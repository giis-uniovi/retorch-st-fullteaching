import { Injectable } from '@angular/core';

@Injectable()
export class AnimationService {

  constructor() { }

  animateIfSmall() {
    if($(window).width() <= 600 || $(window).height() <= 600) $('html,body').animate({scrollTop:0}, this.prefersReducedMotion() ? 0 : 200);
  }

  animateToBottom(selector: string, ms: number) {
    $(selector).stop().animate({
      scrollTop: $(selector)[0].scrollHeight
    }, this.prefersReducedMotion() ? 0 : ms);
  }

  private prefersReducedMotion(): boolean {
    return !!globalThis.matchMedia?.('(prefers-reduced-motion: reduce)').matches;
  }

}
