import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LikeResponse {
  likeCount: number;
  isLiked: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class PostLikeService {
  private baseUrl = 'http://localhost:8080/api/posts';

  constructor(private http: HttpClient) {}

  getLikeInfo(postId: number, userId?: number): Observable<LikeResponse> {
    return this.http.get<LikeResponse>(`${this.baseUrl}/${postId}/likes?userId=${userId}`);
  }


  toggleLike(postId: number, userId: number): Observable<LikeResponse> {
    return this.http.post<LikeResponse>(`${this.baseUrl}/${postId}/like?userId=${userId}`, {});
  }
}
