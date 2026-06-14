import { Component, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';

import { environment } from '../../../environments/environment';

import { MaterializeAction } from '../../shared/materialize.directive';

import { AuthenticationService } from '../../services/authentication.service';
import { LoginModalService } from '../../services/login-modal.service';
import { UserService } from '../../services/user.service';
import { Constants } from '../../constants';

declare const M: any;

@Component({
    selector: 'login-modal',
    templateUrl: './login-modal.component.html',
    styleUrls: ['./login-modal.component.css'],
    standalone: false
})
export class LoginModalComponent {

  email: string;
  password: string;
  confirmPassword: string;
  nickName: string;

  loginView = true;
  fieldsIncorrect = false;
  submitProcessing = false;
  readonly actions = new EventEmitter<string | MaterializeAction>();

  captchaValidated = false;
  readonly captchaPublicKey: string;
  captchaToken: string;

  errorTitle = 'Invalid field';
  errorContent = 'Please check your email or password';
  customClass = 'fail';
  toastMessage = 'Login error! Check your email or password';

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly userService: UserService,
    private readonly router: Router,
    private readonly loginModalService: LoginModalService) {

    this.captchaPublicKey = environment.PUBLIC_RECAPTCHA_KEY;

    this.loginModalService.wat$.subscribe((value) => {
      this.loginView = value;
    });
  }

  setLoginView(option: boolean) {
    this.fieldsIncorrect = false;
    this.loginView = option;
  }

  onSubmit() {
    this.submitProcessing = true;
    if (this.loginView) {
      this.logIn(this.email, this.password);
    } else {
      this.signUp();
    }
  }

  logIn(user: string, pass: string) {
    this.authenticationService.logIn(user, pass).subscribe({
      next: () => {
        this.submitProcessing = false;
        this.fieldsIncorrect = false;
        this.actions.emit({ action: 'modal', params: ['close'] });
        this.router.navigate(['/courses']);
      },
      error: () => {
        this.errorTitle = 'Invalid field';
        this.errorContent = 'Please check your email or password';
        this.customClass = 'fail';
        this.toastMessage = 'Login error! Check your email or password';
        this.handleError();
      }
    });
  }

  signUp() {
    if (!this.captchaValidated) {
      this.errorTitle = 'You must validate the captcha!';
      this.errorContent = '';
      this.customClass = 'fail';
      this.toastMessage = 'Your must validate the captcha!';
      this.handleError();
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.errorTitle = 'Your passwords don\'t match!';
      this.errorContent = '';
      this.customClass = 'fail';
      this.toastMessage = 'Your passwords don\'t match!';
      this.handleError();
      return;
    }

    const regex = new RegExp(Constants.PASS_REGEX);
    if (!regex.exec(this.password)) {
      this.errorTitle = 'Your password does not have a valid format!';
      this.errorContent = 'It must be at least 8 characters long and include one uppercase, one lowercase and a number';
      this.customClass = 'fail';
      this.toastMessage = 'Password must be 8 characters long, one upperCase, one lowerCase and a number';
      this.handleError();
      return;
    }

    this.userService.newUser(this.email, this.password, this.nickName, this.captchaToken).subscribe({
      next: () => { this.logIn(this.email, this.password); },
      error: error => {
        if (error === 409) {
          this.errorTitle = 'Invalid email';
          this.errorContent = 'That email is already in use';
          this.toastMessage = 'That email is already in use!';
        } else if (error === 400) {
          this.errorTitle = 'Invalid password format';
          this.errorContent = 'Our server has rejected that password';
          this.toastMessage = 'That password has not a valid format according to our server!';
        } else if (error === 403) {
          this.errorTitle = 'Invalid email format';
          this.errorContent = 'Our server has rejected that email';
          this.toastMessage = 'That email has not a valid format according to our server!';
        } else if (error === 401) {
          this.errorTitle = 'Captcha not validated!';
          this.errorContent = 'I am sorry, but your bot does not work here :)';
          this.toastMessage = 'You must be a human to sign up here!';
        }
        this.customClass = 'fail';
        this.handleError();
      }
    });
  }

  handleCorrectCaptcha(token: string) {
    console.log('Captcha SUCCESS');
    this.captchaToken = token;
    this.captchaValidated = true;
  }

  handleError() {
    this.submitProcessing = false;
    if (window.innerWidth <= Constants.PHONE_MAX_WIDTH) {
      M.toast({ html: this.toastMessage, classes: 'rounded' });
    } else {
      this.fieldsIncorrect = true;
    }
  }
}
