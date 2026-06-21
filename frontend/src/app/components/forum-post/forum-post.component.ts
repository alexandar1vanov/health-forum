import {Component, DestroyRef, ElementRef, inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ForumPostService} from '../../services/forum-post.service';
import {catchError, map, Observable, of, Subject, switchMap, takeUntil, tap} from 'rxjs';
import {ForumPostResponse} from '../../models/ForumPostResponse';
import {AsyncPipe, DatePipe} from '@angular/common';
import {ActivatedRoute, Router, RouterLink, RouterLinkActive} from '@angular/router';
import {MatMenuModule} from '@angular/material/menu';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {AuthService} from '../../services/AuthService';
import {EditPostDialogComponent} from '../edit-post-dialog/edit-post-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {CommentService} from '../../services/comment.service';
import {PostLikeComponent} from '../post-like/post-like.component';
import {PostRatingResponse} from '../../models/PostRatingResponse';
import {PostRatingService} from '../../services/PostRatingService';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Rating} from 'primeng/rating';
import {FormsModule} from '@angular/forms';
import {PostRatingRequest} from '../../models/PostRatingRequest';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-forum-post',
  imports: [
    AsyncPipe,
    DatePipe,
    RouterLink,
    RouterLinkActive,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    PostLikeComponent,
    Rating,
    FormsModule,
  ],
  templateUrl: './forum-post.component.html',
  styleUrl: './forum-post.component.css'
})
export class ForumPostComponent implements OnInit, OnDestroy {
  fpService = inject(ForumPostService);
  authService = inject(AuthService);
  commentService = inject(CommentService);
  posts$?: Observable<ForumPostResponse[]>;
  route = inject(ActivatedRoute);
  router = inject(Router);
  dialog = inject(MatDialog);
  readonly #postRatingService = inject(PostRatingService);
  readonly #destroyRef = inject(DestroyRef);
  readonly #toastrService = inject(ToastrService);

  searchResults: ForumPostResponse[] | null = null;
  commentCounts: { [postId: number]: Observable<number> } = {};

  hasSearched = false;
  @ViewChild('searchInput') searchInput!: ElementRef<HTMLInputElement>;
  private destroy$ = new Subject<void>();

  currUserId: number | null = null;
  private snackBar: any;
  selectedRatings: PostRatingResponse[] = [];

  ngOnInit(): void {
    this.loadPosts();
    this.loadRatings();
  }

  loadRatings(): void {
    this.#postRatingService.getRatingsByActiveUser().pipe(
      takeUntilDestroyed(this.#destroyRef)
    ).subscribe({
      next: ratings => {
        console.log("ratings:", ratings)
        this.selectedRatings = ratings;
      }
    })
  }

  loadPosts(): void {
    this.posts$?.pipe(
      takeUntilDestroyed(this.#destroyRef)
    ).subscribe(posts => {
      if (posts) {
        posts.forEach(post => {
          this.commentCounts[post.id] = this.commentService.getCommentsCountByPostId(post.id);
        });
      }
    });

    this.fetchPostsByDisease();
  }

  getRating(postId: number): number {
    return this.selectedRatings.find(r => r.postId === postId)?.rating ?? 0;
  }

  submitRating(postId: number, rating: number): void {
    if (!rating) {
      this.#postRatingService.deleteRating(postId).pipe(
        takeUntilDestroyed(this.#destroyRef)
      ).subscribe({
        next: () => {
          this.#toastrService.success("Successfully removed rating!");
        },
        error: (error) => {
          this.#toastrService.error("Error while removing rating!",error);
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
        this.#toastrService.success("Successfully submitted rating!");
      },
      error: (error) => {
        this.#toastrService.error(error);
      }
    })
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  getCommentCounts(postId: number): Observable<number> {
    return this.commentService.getCommentsCountByPostId(postId).pipe(
      catchError(() => of(0))
    );
  }

  searchPosts(content: string): void {
    this.hasSearched = true;
    if (content.trim() === '') {
      this.searchResults = null;
      this.fetchPostsByDisease();
      return;
    }
    this.fpService.getPostsByContent(content).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: data => {
        this.searchResults = data;
      },
      error: error => {
        this.searchResults = [];
      }
    });
  }

  private fetchPostsByDisease(): void {
    this.posts$ = this.route.params.pipe(
      takeUntil(this.destroy$),
      switchMap(params => {
        const diseaseId = +params['id'];
        return diseaseId
          ? this.fpService.getPostsByDiseaseId(diseaseId)
          : this.fpService.getAllPosts();
      }),
      tap(posts => {
        this.commentCounts = {};
        posts.forEach(post => {
          this.commentCounts[post.id] = this.getCommentCounts(post.id);
        });
      })
    );

    this.searchResults = null;
    this.hasSearched = false;
  }

  clearSearch(): void {
    this.hasSearched = false;
    this.searchResults = null;
    this.fetchPostsByDisease();
    this.searchInput.nativeElement.value = '';
  }

  deletePost(postId: number): void {
    this.fpService.deleteForumPost(postId).subscribe({
      next: () => {
        this.posts$ = this.posts$?.pipe(
          map(posts => posts?.filter(post => post.id !== postId) || [])
        );
        if (this.searchResults) {
          this.searchResults = this.searchResults.filter(post => post.id !== postId);
          if (this.searchResults.length === 0) {
            this.clearSearch();
          }
        }
      },
      error: (error) => {
        console.error('Error deleting post:', error);
        let errorMessage = 'Failed to delete post. Please try again.';
        if (error.status === 403) {
          errorMessage = 'You do not have permission to delete this post.';
        } else if (error.status === 404) {
          errorMessage = 'Post not found or already deleted.';
        }
      }
    });
  }

  editPost(postId: number): void {
    this.fpService.getPostById(postId).pipe(
      catchError(error => {
        console.error('Error fetching post:', error);
        this.snackBar.open('Could not load post for editing', 'Close', {duration: 3000});
        return of(null);
      })
    ).subscribe(post => {
      if (!post) return;
      const dialogRef = this.dialog.open(EditPostDialogComponent, {
        width: '600px',
        data: {post}
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.fpService.updateForumPost(postId, {
            title: result.title,
            content: result.content
          }).pipe(
            catchError(error => {
              console.error('Error updating post:', error);
              this.snackBar.open('Failed to update post', 'Close', {duration: 5000});
              return of(null);
            })
          ).subscribe(updatedPost => {
            if (!updatedPost) return;
            this.posts$ = this.posts$?.pipe(
              map(posts => {
                if (!posts) return [];
                return posts.map(p => p.id === postId ? updatedPost : p);
              })
            );
            this.snackBar.open('Post updated successfully', 'Close', {duration: 5000});
          });
        }
      });
    });
  }
}
