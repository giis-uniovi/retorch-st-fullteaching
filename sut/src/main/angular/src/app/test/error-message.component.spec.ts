import { TestBed } from '@angular/core/testing';
import { ErrorMessageComponent } from '../components/error-message/error-message.component';

describe('ErrorMessageComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ErrorMessageComponent],
    });
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(ErrorMessageComponent);
    expect(fixture.componentInstance).toBeDefined();
  });

  it('should display errorTitle in the template', () => {
    const fixture = TestBed.createComponent(ErrorMessageComponent);
    fixture.componentInstance.errorTitle = 'Something went wrong';
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('Something went wrong');
  });

  it('should display errorContent in the template', () => {
    const fixture = TestBed.createComponent(ErrorMessageComponent);
    fixture.componentInstance.errorContent = 'Please try again';
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('Please try again');
  });
});
