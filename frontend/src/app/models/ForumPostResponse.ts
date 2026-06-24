import {DiseaseResponse} from "./DiseaseResponse";
import {UserResponse} from './UserResponse';

export interface ForumPostResponse {
  id: number;
  title: string;
  content: string;
  diseases: DiseaseResponse[];
  createdAt: Date;
  updatedAt: Date;
  user: UserResponse;
  likeCount: number;
  rating: number;
  isLiked: number;
}
