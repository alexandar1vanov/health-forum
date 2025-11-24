import {User} from "./User";
import {Disease} from "./Disease";

export interface ForumPost {
  id: number;
  title: string;
  content: string;
  diseases: Disease[];
  createdAt: Date;
  updatedAt: Date;
  user: User;
}
