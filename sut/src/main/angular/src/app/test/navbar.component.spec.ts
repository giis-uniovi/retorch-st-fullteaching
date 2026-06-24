import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { By } from '@angular/platform-browser';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { NavbarComponent } from '../components/navbar/navbar.component';
import { AuthenticationService } from '../services/authentication.service';
import { LoginModalService } from '../services/login-modal.service';

class MockAuthenticationService {
  logIn(_email: string, _pass: string) { return of(true); }
  isLoggedIn() { return false; }
  getCurrentUser() { return null; }
}

class MockLoginModalService {
  wat$ = { subscribe: () => {} };
  activateLoginView(_b: boolean) { /* mock */ }
}

describe('NavbarComponent', () => {
  let comp: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [NavbarComponent],
      providers: [
        provideRouter([]),
        { provide: AuthenticationService, useClass: MockAuthenticationService },
        { provide: LoginModalService, useClass: MockLoginModalService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    });

    fixture = TestBed.createComponent(NavbarComponent);
    comp = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(comp).toBeDefined();
  });

  it('should display the app title in the logo', () => {
    const logoEl: HTMLElement = fixture.debugElement.query(By.css('#logo-container')).nativeElement;
    expect(logoEl.textContent).toContain('FullTeaching');
  });

  it('should show login links when user is not logged in', () => {
    const loginLinks = fixture.debugElement.queryAll(By.css('a[href="#login-modal"]'));
    expect(loginLinks.length).toBeGreaterThan(0);
  });
});
