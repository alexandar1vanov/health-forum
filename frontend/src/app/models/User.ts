import {Role} from './enums/Role';

export interface User {
  id: number;
  email: string;
  role: Role,
  hasSelectedDiseases: boolean;
  createdAt?: Date;
}
