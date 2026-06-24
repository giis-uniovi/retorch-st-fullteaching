import { Component } from '@angular/core';
import { Location } from '@angular/common';
import { RouterLink } from '@angular/router';

import { AuthenticationService } from '../../services/authentication.service';
import { LoginModalService } from '../../services/login-modal.service';
import { MaterializeModule } from '../../shared/materialize.module';

@Component({
    selector: 'navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.css'],
    imports: [RouterLink, MaterializeModule]
})
export class NavbarComponent {

  constructor(readonly authenticationService: AuthenticationService, private readonly loginModalService: LoginModalService, private readonly location: Location) { }

  updateLoginModalView(b: boolean){
    this.loginModalService.activateLoginView(b);
  }

  public addSessionHidden() {
    const list = ['/courses'];
    const route = this.location.path();
    return list.includes(route);
  }

  logout(){
    this.authenticationService.logOut().subscribe(
  		response => { $("div.drag-target").remove(); },
  		error => console.log("Error when trying to log out: " + error)
  	);
  }

}
