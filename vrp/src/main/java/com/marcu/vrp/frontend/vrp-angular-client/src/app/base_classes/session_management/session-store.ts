import {Store, StoreConfig} from "@datorama/akita";
import {Injectable} from "@angular/core";
import * as storage from "./my-session-storage";

export type SessionState = {
  id: number;
  username: string;
  role: string;
}

export function createInitialSessionState(): SessionState {
  return {
    id: null,
    username: null,
    role: null,
    ...storage.getSession(),
  }
}

@StoreConfig({ name: "session" })
@Injectable({
  providedIn: 'root'
})
export class SessionStore extends Store<SessionState> {
  constructor(
  ) {
    super(createInitialSessionState());
  }

  login(session: SessionState) {
    this.update(session);
    storage.saveSession(session);
  }

  logout() {
    storage.clearSession();
    this.update(createInitialSessionState());
  }
}
