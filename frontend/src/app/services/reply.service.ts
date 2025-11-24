import {inject, Injectable, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthService} from './AuthService';
import {Reply} from '../models/Reply';
import {Observable} from 'rxjs';
import {CreateReply} from '../models/CreateReply';

@Injectable({
  providedIn: 'root'
})
export class ReplyService{
  private readonly url='http://localhost:8080/api/reply';
  http = inject(HttpClient);
  authService = inject(AuthService);

  getRepliesByCommentId(commentId: number): Observable<Reply[]> {
    return this.http.get<Reply[]>(`${this.url}/comment/${commentId}`);
  }

  postReply(reply: CreateReply): Observable<Reply> {
    return this.http.post<Reply>(this.url, reply);
  }

  updateReply(id: number, content: string): Observable<Reply> {
    return this.http.put<Reply>(`${this.url}/${id}`, { content });
  }

  deleteReply(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

}
