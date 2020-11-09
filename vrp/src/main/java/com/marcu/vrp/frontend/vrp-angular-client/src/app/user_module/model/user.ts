import {Role} from "./role";

export class User {
  id: number;
  username: string;
  email: string;
  phone: string;
  firstName: string;
  lastName: string;
  roleId: number;
  role: Role;
}
