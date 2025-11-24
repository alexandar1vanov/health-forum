import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {RouterLink, RouterLinkActive} from "@angular/router";
import {ForumPostService} from '../../services/forum-post.service';
import {Observable, startWith, Subject, switchMap, takeUntil} from 'rxjs';
import {ForumPost} from '../../models/ForumPost';
import {AuthService} from '../../services/AuthService';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-user-posts',
  imports: [
    RouterLinkActive,
    RouterLink,
    AsyncPipe
  ],
  templateUrl: './user-posts.component.html',
  styleUrl: './user-posts.component.css'
})
export class UserPostsComponent implements OnInit, OnDestroy {
  fpService = inject(ForumPostService);
  authService = inject(AuthService);
  userPosts$?: Observable<ForumPost[]>;

  private destroy$ = new Subject<void>();

  ngOnInit(): void {
    const currUserId = this.authService.getLoggedInUserId();
    if (currUserId !== null) {
      this.userPosts$ = this.fpService.postsRefresh$.pipe(
        startWith(true),
        switchMap(() => this.fpService.getPostsByUserId(currUserId)),
        takeUntil(this.destroy$)
      )
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
