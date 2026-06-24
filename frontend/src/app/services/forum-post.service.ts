import {inject, Injectable, OnInit} from '@angular/core';
import {AuthService} from './AuthService';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {ForumPostResponse} from '../models/ForumPostResponse';
import {CreateForum} from '../models/CreateForum';

@Injectable({
  providedIn: 'root'
})
export class ForumPostService implements OnInit {
  private readonly url = '/api/forum';
  private authService = inject(AuthService);
  http = inject(HttpClient);

  currentUser: number | null | undefined;

  private postsRefreshSubject = new BehaviorSubject<boolean>(false);
  postsRefresh$ = this.postsRefreshSubject.asObservable()

  private userPostsCache = new Map<number, ForumPostResponse[]>()

  ngOnInit(): void {
    this.currentUser = this.authService.getLoggedInUserId();
  }

  getAllPosts(): Observable<ForumPostResponse[]> {
    return this.http.get<ForumPostResponse[]>(`${this.url}`);
  }

  getPostById(postId: number): Observable<ForumPostResponse> {
    return this.http.get<ForumPostResponse>(`${this.url}/post/${postId}`);
  }

  getPostsByUserId(userId: number): Observable<ForumPostResponse[]> {
    return this.http.get<ForumPostResponse[]>(`${this.url}/user/${userId}`)
      .pipe(
        tap(posts => {
          this.userPostsCache.set(userId, posts);
        })
      );
  }

  getPostsByDiseaseId(diseaseId: number): Observable<ForumPostResponse[]> {
    return this.http.get<ForumPostResponse[]>(`${this.url}/disease/${diseaseId}`);
  }

  getPostsByContent(content: string): Observable<ForumPostResponse[]> {
    return this.http.get<ForumPostResponse[]>(`${this.url}/content/${content}`);
  }

  createForumPost(postData: CreateForum): Observable<ForumPostResponse> {
    return this.http.post<ForumPostResponse>(`${this.url}`, postData)
      .pipe(
        tap(() => {
            this.notifyPostsChanged()
          }
        )
      )
  }

  deleteForumPost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/post/${postId}`)
      .pipe(
        tap(() => {
          this.notifyPostsChanged()
          this.removePostFromCache(postId)
        })
      );
  }

  updateForumPost(postId: number, postData: { title: string, content: string }): Observable<ForumPostResponse> {
    return this.http.put<ForumPostResponse>(`${this.url}/post/${postId}`, postData)
      .pipe(
        tap((updatedPost) => {
          this.notifyPostsChanged()
          this.updatePostInCache(updatedPost)
        })
      );
  }

  notifyPostsChanged(): void {
    this.postsRefreshSubject.next(true)
  }

  getCachedUserPosts(userId: number): ForumPostResponse[] | null {
    return this.userPostsCache.get(userId) || null;
  }

  private updatePostInCache(updatedPost: ForumPostResponse): void {
    this.userPostsCache.forEach((posts, userId) => {
      const postIndex = posts.findIndex(post => post.id === updatedPost.id);
      if (postIndex !== -1) {
        const updatedPosts = [...posts];
        updatedPosts[postIndex] = updatedPost;
        this.userPostsCache.set(userId, updatedPosts);
      }
    });
  }

  private removePostFromCache(postId: number): void {
    this.userPostsCache.forEach((posts, userId) => {
      const updatedPosts = posts.filter(post => post.id !== postId);
      this.userPostsCache.set(userId, updatedPosts);
    });
  }
}
