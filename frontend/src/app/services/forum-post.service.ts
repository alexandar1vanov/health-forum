import {inject, Injectable, OnInit} from '@angular/core';
import {AuthService} from './AuthService';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {ForumPost} from '../models/ForumPost';
import {CreateForum} from '../models/CreateForum';

@Injectable({
  providedIn: 'root'
})
export class ForumPostService implements OnInit {
  private readonly url = 'http://localhost:8080/api/forum';
  private authService = inject(AuthService);
  http = inject(HttpClient);

  currentUser: number | null | undefined;

  private postsRefreshSubject = new BehaviorSubject<boolean>(false);
  postsRefresh$ = this.postsRefreshSubject.asObservable()

  private userPostsCache = new Map<number, ForumPost[]>()

  ngOnInit(): void {
    this.currentUser = this.authService.getLoggedInUserId();
  }

  getAllPosts(): Observable<ForumPost[]> {
    return this.http.get<ForumPost[]>(`${this.url}`);
  }

  getPostById(postId: number): Observable<ForumPost> {
    return this.http.get<ForumPost>(`${this.url}/post/${postId}`);
  }

  getPostsByUserId(userId: number): Observable<ForumPost[]> {
    return this.http.get<ForumPost[]>(`${this.url}/user/${userId}`)
      .pipe(
        tap(posts => {
          this.userPostsCache.set(userId, posts);
        })
      );
  }

  getPostsByDiseaseId(diseaseId: number): Observable<ForumPost[]> {
    return this.http.get<ForumPost[]>(`${this.url}/disease/${diseaseId}`);
  }

  getPostsByContent(content: string): Observable<ForumPost[]> {
    return this.http.get<ForumPost[]>(`${this.url}/content/${content}`);
  }

  createForumPost(postData: CreateForum): Observable<ForumPost> {
    return this.http.post<ForumPost>(`${this.url}`, postData)
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

  updateForumPost(postId: number, postData: { title: string, content: string }): Observable<ForumPost> {
    return this.http.put<ForumPost>(`${this.url}/post/${postId}`, postData)
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

  getCachedUserPosts(userId: number): ForumPost[] | null {
    return this.userPostsCache.get(userId) || null;
  }

  private updatePostInCache(updatedPost: ForumPost): void {
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
