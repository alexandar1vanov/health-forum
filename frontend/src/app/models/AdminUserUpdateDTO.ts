import {Role} from './enums/Role';

export interface AdminUserUpdateDTO {
  email?: string;
  role?: Role;
}
