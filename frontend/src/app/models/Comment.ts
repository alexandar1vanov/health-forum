import {User} from './User';
import {ForumPost} from './ForumPost';
import {Reply} from './Reply';

export interface Comment {
  id: number;
  user: User
  forumPost: ForumPost
  content: string
  createdAt: Date
  updatedAt: Date
  replies?: Reply[];
  showReplies?: boolean;
  isReplyingTo?: boolean;
}
