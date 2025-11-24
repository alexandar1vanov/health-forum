import {Component, inject, OnInit, OnDestroy, NgZone} from '@angular/core';
import {AsyncPipe, DatePipe} from "@angular/common";
import {ForumPost} from '../../models/ForumPost';
import {ForumPostService} from '../../services/forum-post.service';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';
import {BehaviorSubject, catchError, EMPTY, map, of, Subject, switchMap, takeUntil, tap, throwError} from 'rxjs';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {UserPostsComponent} from '../user-posts/user-posts.component';
import {MatIconButton} from "@angular/material/button";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {AuthService} from '../../services/AuthService';
import {MatIcon} from '@angular/material/icon';
import {EditPostDialogComponent} from '../edit-post-dialog/edit-post-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {CommentSectionComponent} from '../comment-section/comment-section.component';
import {PostLikeComponent} from '../post-like/post-like.component';

@Component({
  selector: 'app-forum-post-details',
  imports: [
    DatePipe,
    AsyncPipe,
    SidebarComponent,
    UserPostsComponent,
    RouterOutlet,
    MatIconButton,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    MatIcon,
    CommentSectionComponent,
    PostLikeComponent
  ],
  templateUrl: './forum-post-details.component.html',
  styleUrls: ['./forum-post-details.component.css']
})
export class ForumPostDetailsComponent implements OnInit, OnDestroy {
  route = inject(ActivatedRoute);
  fpService = inject(ForumPostService);
  authService = inject(AuthService);
  ngZone = inject(NgZone);

  private postSubject = new BehaviorSubject<ForumPost | null>(null);
  post$ = this.postSubject.asObservable().pipe(
    map(post => post as ForumPost)
  );
  router = inject(Router);
  dialog = inject(MatDialog);
  snackBar = inject(MatSnackBar);
  private destroy$ = new Subject<void>();

  currUserId: number | null = null;

  ngOnInit() {
    this.currUserId = this.authService.getLoggedInUserId();
    this.fetchPost();
  }

  private fetchPost(): void {
    this.route.paramMap.pipe(
      takeUntil(this.destroy$),
      switchMap(paramMap => {
        const postId = this.route.snapshot.paramMap.get('id');
        if (postId) {
          console.log("Fetching post for postId", postId);
          return this.fpService.getPostById(+postId);
        } else {
          console.error("No postId found in route paramMap!");
          return EMPTY;
        }
      })
    ).subscribe(post => {
      this.postSubject.next(post);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.postSubject.complete();
  }

  deletePost(postId: number): void {
    this.fpService.deleteForumPost(postId).pipe(
      tap(() => {
        this.ngZone.run(() => {
          this.router.navigate(['/home']);
          this.snackBar.open('Post deleted successfully', 'Close', { duration: 3000 });
        });
      }),
      catchError((error) => {
        console.error('Error deleting post:', error);
        let errorMessage = 'Failed to delete post. Please try again.';
        if (error.status === 403) {
          errorMessage = 'You do not have permission to delete this post.';
        } else if (error.status === 404) {
          errorMessage = 'Post not found or already deleted.';
        }
        this.snackBar.open(errorMessage, 'Close', { duration: 5000 });
        return throwError(() => error);
      })
    ).subscribe();
  }


  editPost(postId: number): void {
    const currentPost = this.postSubject.getValue();
    if (!currentPost) {
      this.snackBar.open('Could not load post for editing', 'Close', { duration: 3000 });
      return;
    }

    const dialogRef = this.dialog.open(EditPostDialogComponent, {
      width: '600px',
      data: { post: currentPost }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const updatedPost = {
          ...currentPost,
          title: result.title,
          content: result.content
        };
        this.postSubject.next(updatedPost);

        this.fpService.updateForumPost(postId, {
          title: result.title,
          content: result.content
        }).pipe(
          catchError(error => {
            console.error('Error updating post:', error);
            this.postSubject.next(currentPost);
            this.snackBar.open('Failed to update post', 'Close', { duration: 3000 });
            return of(null);
          })
        ).subscribe(serverUpdatedPost => {
          if (!serverUpdatedPost) return;
          this.snackBar.open('Post updated successfully', 'Close', { duration: 3000 });
        });
      }
    });
  }
}
