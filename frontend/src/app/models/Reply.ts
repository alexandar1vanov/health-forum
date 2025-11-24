import {User} from './User';

export interface Reply {
  id: number,
  content: string,
  user: User,
  comment: Comment,
  createdAt: Date,
}
