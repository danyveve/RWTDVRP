import {SessionState} from "./session-store";


export function getSession() {
  let username =  sessionStorage.getItem("session.username");
  let rawid = sessionStorage.getItem("session.id");
  let id = rawid ? +rawid : rawid;
  let role = sessionStorage.getItem("session.role");
  let sessionState = {username: username, id: id, role: role} as SessionState;
  console.log(sessionState);
  return sessionState;
}

export function saveSession(session: SessionState) {
  sessionStorage.setItem("session.username", session.username);
  sessionStorage.setItem("session.id", session.id.toString());
  sessionStorage.setItem("session.role", session.role);
}

export function clearSession() {
  sessionStorage.removeItem("session.username");
  sessionStorage.removeItem("session.id");
  sessionStorage.removeItem("session.role");

}
