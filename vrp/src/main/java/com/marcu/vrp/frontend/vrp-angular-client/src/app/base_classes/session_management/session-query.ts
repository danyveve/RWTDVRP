import {Query, toBoolean} from '@datorama/akita';
import {SessionState, SessionStore} from "./session-store";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class SessionQuery extends Query<SessionState> {
  isLoggedIn$ = this.select(state => {
    return toBoolean(state.id);
  });
  username$ = this.select(state => state.username);
  role$ = this.select(state => state.role);
  id$ = this.select(state => state.id);


  constructor(protected store: SessionStore) {
    super(store);
  }

  isLoggedIn() {
    return toBoolean(this.getValue().id);
  }

  isAdmin() {
    return this.getValue().role && this.getValue().role !== "CLIENT"
  }
}
