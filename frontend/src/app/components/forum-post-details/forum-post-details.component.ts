import {Component, DestroyRef, inject, NgZone, OnInit} from '@angular/core';
import {AsyncPipe, DatePipe} from "@angular/common";
import {ForumPostResponse} from '../../models/ForumPostResponse';
import {ForumPostService} from '../../services/forum-post.service';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';
import {BehaviorSubject, catchError, EMPTY, map, of, switchMap, tap, throwError} from 'rxjs';
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
import {Rating} from 'primeng/rating';
import {FormsModule} from '@angular/forms';
import {PostRatingRequest} from '../../models/PostRatingRequest';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {PostRatingService} from '../../services/PostRatingService';
import {ToastrService} from 'ngx-toastr';

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
    PostLikeComponent,
    Rating,
    FormsModule
  ],
  templateUrl: './forum-post-details.component.html',
  styleUrls: ['./forum-post-details.component.css']
})
export class ForumPostDetailsComponent implements OnInit {
  route = inject(ActivatedRoute);
  fpService = inject(ForumPostService);
  authService = inject(AuthService);
  ngZone = inject(NgZone);

  private postSubject = new BehaviorSubject<ForumPostResponse | null>(null);
  post$ = this.postSubject.asObservable().pipe(
    map(post => post as ForumPostResponse)
  );
  router = inject(Router);
  dialog = inject(MatDialog);
  snackBar = inject(MatSnackBar);
  readonly #postRatingService = inject(PostRatingService);
  readonly #destroyRef = inject(DestroyRef);
  readonly #toastrService = inject(ToastrService);

  currUserId: number | null = null;
  currentRating: number = 0;

  ngOnInit() {
    this.currUserId = this.authService.getLoggedInUserId();
    this.fetchPost();
    this.fetchRating();
  }

  private fetchPost(): void {
    this.route.paramMap.pipe(
      switchMap(paramMap => {
        const postId = this.route.snapshot.paramMap.get('id');
        if (postId) {
          console.log("Fetching post for postId", postId);
          return this.fpService.getPostById(+postId);
        } else {
          console.error("No postId found in route paramMap!");
          return EMPTY;
        }
      }),
      takeUntilDestroyed(this.#destroyRef)
    ).subscribe(post => {
      this.postSubject.next(post);
    });
  }

  fetchRating(){
    this.route.paramMap.pipe(
      switchMap(() => {
        const postId = this.route.snapshot.paramMap.get('id');
        if (postId) return this.#postRatingService.getRatingByPostId(parseInt(postId))
        return EMPTY
      }),
      takeUntilDestroyed(this.#destroyRef)
    ).subscribe({
      next: ratingResponse => {
        this.currentRating = ratingResponse.rating;
      },
      error: err => {
        this.#toastrService.error(err)
      }
    })
  }

  submitRating(postId: number, rating: number | null): void {
    if (!rating) {
      this.#postRatingService.deleteRating(postId).pipe(
        takeUntilDestroyed(this.#destroyRef)
      ).subscribe({
        next: () => {
          this.currentRating = 0;
          this.#toastrService.success("Removed your rating!");
        },
        error: (error) => {
          this.#toastrService.error(error);
        }
      })
      return;
    }

    const postRatingRequest: PostRatingRequest = {
      postId: postId,
      rating: rating
    };
    this.#postRatingService.submitRating(postRatingRequest).pipe(
      takeUntilDestroyed(this.#destroyRef)
    ).subscribe({
      next: () => {
        this.currentRating = rating;
        this.#toastrService.success("Successfully submitted rating!");
      },
      error: (error) => {
        this.#toastrService.error(error);
      }
    })
  }

  deletePost(postId: number): void {
    this.fpService.deleteForumPost(postId).pipe(
      tap(() => {
        this.ngZone.run(() => {
          this.router.navigate(['/home']);
          this.snackBar.open('Post deleted successfully', 'Close', {duration: 3000});
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
        this.snackBar.open(errorMessage, 'Close', {duration: 5000});
        return throwError(() => error);
      }),
      takeUntilDestroyed(this.#destroyRef)
    ).subscribe();
  }


  editPost(postId: number): void {
    const currentPost = this.postSubject.getValue();
    if (!currentPost) {
      this.snackBar.open('Could not load post for editing', 'Close', {duration: 3000});
      return;
    }

    const dialogRef = this.dialog.open(EditPostDialogComponent, {
      width: '600px',
      data: {post: currentPost}
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
            this.snackBar.open('Failed to update post', 'Close', {duration: 3000});
            return of(null);
          }),
          takeUntilDestroyed(this.#destroyRef)
        ).subscribe(serverUpdatedPost => {
          if (!serverUpdatedPost) return;
          this.snackBar.open('Post updated successfully', 'Close', {duration: 3000});
        });
      }
    });
  }
}
