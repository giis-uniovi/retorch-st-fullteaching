import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { FooterComponent } from './components/footer/footer.component';

@Component({
    selector: 'app',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    imports: [NavbarComponent, RouterOutlet, FooterComponent]
})
export class AppComponent {

  constructor(private readonly router: Router){}

  isVideoSessionUrl(){
    return this.router.url.startsWith('/session/');
  }

}
