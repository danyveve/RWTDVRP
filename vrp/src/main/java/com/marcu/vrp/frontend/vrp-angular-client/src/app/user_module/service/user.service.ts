import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {User} from "../model/user";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userLink = "http://localhost:8080/api/user";

  constructor(
    private httpClient: HttpClient
  ) { }

  getCurrentUser(): Observable<User> {
    return this.httpClient.get<User>(this.userLink + "/get/current");
  }
}
