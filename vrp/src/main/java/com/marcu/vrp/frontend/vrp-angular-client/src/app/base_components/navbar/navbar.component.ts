import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {SessionStore} from "../../base_classes/session_management/session-store";
import {SessionQuery} from "../../base_classes/session_management/session-query";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  constructor(
    public router: Router,
    private httpClient: HttpClient,
    private authStore: SessionStore,
    private authQuery: SessionQuery
  ) {
  }

  ngOnInit(): void {

  }

  logout() {
    this.httpClient.post(`http://localhost:8080/logout`, null).subscribe(
      (result) => {
        this.authStore.logout();
        this.router.navigateByUrl("/home");
      }
    );
  }
}
