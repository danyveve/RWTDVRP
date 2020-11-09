import {CanActivate, Router} from "@angular/router";
import {SessionQuery} from "./session-query";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class LoggedInGuard implements CanActivate {
  constructor(private query:SessionQuery,
              private router: Router) {
  }
  canActivate() {
    if (this.query.isLoggedIn()) {
      return true;
    } else {
      this.router.navigateByUrl("/login");
    }
    return false;
  }
}
