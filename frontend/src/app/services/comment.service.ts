import {inject, Injectable, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable, tap} from 'rxjs';
import {Comment} from '../models/Comment';
import {AuthService} from './AuthService';

@Injectable({
  providedIn: 'root'
})
export class CommentService implements OnInit {
  private readonly url = 'http://localhost:8080/api/comments';
  http = inject(HttpClient);
  authService = inject(AuthService);

  currentUser: number | null | undefined;

  ngOnInit(): void {
    this.currentUser = this.authService.getLoggedInUserId();
  }

  getCommentsByPostId(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.url}/forumPost/${postId}`);
  }

  postComment(commentData: { userId: number, forumPostId: number, content: string }): Observable<Comment> {
    return this.http.post<Comment>(`${this.url}`, commentData);
  }

  updateComment(commentId: number, content: string): Observable<Comment> {
    return this.http.put<Comment>(`${this.url}/${commentId}`, { content });
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${commentId}`);
  }

  getCommentsCountByPostId(postId: number): Observable<number> {
    return this.getCommentsByPostId(postId).pipe(
      tap(comments => console.log('Comments: ', comments)),
      map(comments => comments.length)
    );
  }

}
