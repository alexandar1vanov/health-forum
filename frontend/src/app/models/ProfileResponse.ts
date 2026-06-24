export interface ProfileResponse {
  id: number;
  email: string;
  name: string;
  surname: string;
  diseases: string[];
  hasSelectedDiseases: boolean;
  createdAt?: Date;
}
