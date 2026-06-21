import {User} from './User';
import {ForumPostResponse} from './ForumPostResponse';
import {Reply} from './Reply';

export interface Comment {
  id: number;
  user: User
  forumPost: ForumPostResponse
  content: string
  createdAt: Date
  updatedAt: Date
  replies?: Reply[];
  showReplies?: boolean;
  isReplyingTo?: boolean;
}
