import { TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter, RouterOutlet } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { AppComponent } from '../app.component';

describe('AppComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [provideRouter([])],
      schemas: [NO_ERRORS_SCHEMA],
    });
    TestBed.overrideComponent(AppComponent, { set: { imports: [RouterOutlet], schemas: [NO_ERRORS_SCHEMA] } });
  });

  it('should create', waitForAsync(() => {
    const fixture = TestBed.createComponent(AppComponent);
    expect(fixture.componentInstance).toBeDefined();
  }));

  it('should render a router-outlet', waitForAsync(() => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelector('router-outlet')).not.toBeNull();
  }));
});
