import {Component, ElementRef, inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {CommentService} from '../../services/comment.service';
import {catchError, filter, forkJoin, map, Observable, of, Subject, switchMap, takeUntil, tap, take} from 'rxjs';
import {Comment} from '../../models/Comment';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {AsyncPipe, DatePipe} from '@angular/common';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../services/AuthService';
import {CreateComment} from '../../models/CreateComment';
import {GeminiAiService} from '../../services/GeminiAiService';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatIconButton} from '@angular/material/button';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {ReplyService} from '../../services/reply.service';
import {ReplyComponent} from '../reply/reply.component';

interface CommentWithReplies extends Comment {
  showReplies?: boolean;
  isReplyingTo?: boolean;
  replies?: any[];
}

interface ContentReviewResult {
  isSpam: boolean;
  isRelevant: boolean;
  reason?: string;
}

@Component({
  selector: 'app-comment-section',
  imports: [
    MatIcon,
    AsyncPipe,
    DatePipe,
    ReactiveFormsModule,
    MatIconButton,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
    FormsModule,
    ReplyComponent,
  ],
  templateUrl: 'comment-section.component.html',
  styleUrl: './comment-section.component.css'
})
export class CommentSectionComponent implements OnInit, OnDestroy {
  comments$: Observable<CommentWithReplies[]> = of([]);

  commentService = inject(CommentService);
  authService = inject(AuthService);
  replyService = inject(ReplyService);
  route = inject(ActivatedRoute);
  router = inject(Router);
  geminiService = inject(GeminiAiService);
  snackBar = inject(MatSnackBar);
  formBuilder = inject(FormBuilder);

  private destroy$ = new Subject<void>();
  private focusListener: any;

  commentForm!: FormGroup;
  isPosted: boolean = false;
  forumPostId: number | null = null;
  currUserId: number | null = null;
  currUserEmail: string | null = null;
  editingCommentId: number = -1;
  editedContent: string = '';

  postTitle: string = '';
  isCheckingSpam: boolean = false;
  isEditingInProgress: boolean = false;

  @ViewChild('commentInput') commentInput!: ElementRef<HTMLTextAreaElement>;

  ngOnInit(): void {
    this.initForm();
    this.loadUserInfo();
    this.setupRouteListener();

    this.focusListener = this.handleFocusEvent.bind(this);
    document.addEventListener('focus-comment-input', this.focusListener);
  }

  ngOnDestroy(): void {
    document.removeEventListener('focus-comment-input', this.focusListener);
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.commentForm = this.formBuilder.group({
      content: ['', [Validators.required, Validators.maxLength(1000)]]
    });
  }

  private loadUserInfo(): void {
    this.currUserId = this.authService.getLoggedInUserId();
    this.currUserEmail = this.authService.getLoggedInUserEmail();
  }

  private setupRouteListener(): void {
    this.loadPostIdAndComments();
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.loadPostIdAndComments();
    });
  }

  loadPostIdAndComments(): void {
    this.route.params.pipe(
      takeUntil(this.destroy$)
    ).subscribe(params => {
      const newPostId = +params['id'];
      if (this.forumPostId !== newPostId) {
        this.comments$ = of([]);
        this.forumPostId = newPostId;
        if (this.forumPostId) {
          console.log('Loading comments for post ID:', this.forumPostId);
          this.fetchComments();
          this.fetchPostContext();
          if (this.commentForm) {
            this.commentForm.reset();
          }
          this.editingCommentId = -1;
          this.editedContent = '';
        }
      }
    });
  }

  private fetchComments(): void {
    if (!this.forumPostId) {
      console.error('Cannot fetch comments: postId is null');
      this.comments$ = of([]);
      return;
    }
    this.comments$ = this.commentService.getCommentsByPostId(this.forumPostId).pipe(
      map(comments => this.enrichCommentsWithUIState(comments)),
      switchMap(comments => {
        if (comments.length === 0) {
          return of([] as CommentWithReplies[]);
        }
        return this.fetchRepliesForComments(comments);
      }),
      tap(comments => {
        console.log(`Loaded ${comments.length} comments for post ID: ${this.forumPostId}`);
      }),
      catchError(error => {
        console.error('Error fetching comments:', error);
        this.showErrorNotification('Failed to load comments. Please try again.');
        return of([]);
      })
    );
  }

  private enrichCommentsWithUIState(comments: Comment[]): CommentWithReplies[] {
    return comments.map(comment => ({
      ...comment,
      showReplies: false,
      isReplyingTo: false,
      replies: []
    }));
  }

  private fetchRepliesForComments(comments: CommentWithReplies[]): Observable<CommentWithReplies[]> {
    const replyRequests = comments.map(comment =>
      this.replyService.getRepliesByCommentId(comment.id).pipe(
        map(replies => ({commentId: comment.id, replies})),
        catchError(error => {
          console.error(`Error fetching replies for comment ${comment.id}:`, error);
          return of({commentId: comment.id, replies: []});
        })
      )
    );
    if (replyRequests.length === 0) {
      return of([]);
    }

    return forkJoin(replyRequests).pipe(
      map(results => comments.map(comment => {
        const resultItem = results.find(r => r.commentId === comment.id);
        return resultItem ? {...comment, replies: resultItem.replies} : comment;
      }))
    );
  }

  fetchPostContext(): void {
    if (!this.forumPostId) {
      return;
    }
  }

  handleFocusEvent(): void {
    setTimeout(() => {
      if (this.commentInput && this.commentInput.nativeElement) {
        this.commentInput.nativeElement.focus();
        this.commentInput.nativeElement.scrollIntoView({behavior: 'smooth'});
        console.log('Comment input focused');
      } else {
        console.error('Comment input element not found');
      }
    }, 100);
  }

  onSubmit(): void {
    if (this.commentForm.invalid) {
      return;
    }

    if (this.isPosted || this.isCheckingSpam) {
      return;
    }

    if (!this.forumPostId || !this.currUserId) {
      console.error('Cannot post comment: missing postId or userId');
      return;
    }

    const content = this.commentForm.get('content')?.value?.trim();
    if (!content) {
      return;
    }

    this.reviewContent(content, this.postComment.bind(this));
  }

  private reviewContent(content: string, onSuccess: (content: string) => void): void {
    this.isCheckingSpam = true;
    const postContext = `health forum post about ${this.postTitle}`;

    this.geminiService.reviewContent(content, postContext).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (result: ContentReviewResult) => {
        this.isCheckingSpam = false;
        console.log('Gemini review result:', result);

        if (result.isSpam) {
          this.handleContentViolation(
            'spam',
            `Your comment has been flagged as potential spam: ${result.reason}`,
            'Comment has been flagged as spam. Please revise your content.'
          );
          return;
        }

        if (!result.isRelevant) {
          this.handleContentViolation(
            'relevance',
            `Your comment appears to be off-topic: ${result.reason}`,
            'Comment is not relevant to this discussion. Please ensure your comment is on-topic.'
          );
          return;
        }

        onSuccess(content);
      },
      error: (error) => {
        console.error('Error reviewing content:', error);
        this.isCheckingSpam = false;
        this.showErrorNotification('We encountered an issue reviewing your content. Please try again.');
      }
    });
  }

  private handleContentViolation(type: string, notification: string, formError: string): void {
    this.snackBar.open(
      notification,
      'Close',
      {duration: 8000, panelClass: 'warning-snackbar'}
    );
    this.commentForm.get('content')?.setErrors({
      serverError: formError
    });
  }

  private postComment(content: string): void {
    this.isPosted = true;

    const commentRequest: CreateComment = {
      userId: this.currUserId!,
      forumPostId: this.forumPostId!,
      content: content
    };

    this.commentService.postComment(commentRequest).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: () => {
        this.commentForm.reset();
        this.isPosted = false;
        this.fetchComments();
        this.showSuccessNotification('Comment posted successfully');
      },
      error: (error) => {
        console.error('Error posting comment:', error);
        this.isPosted = false;
        this.handleCommentError(error);
      }
    });
  }

  private handleCommentError(error: any): void {
    if (error.status === 400 && error.error?.content) {
      this.commentForm.get('content')?.setErrors({
        serverError: error.error.content
      });
    } else {
      this.showErrorNotification('Error posting comment. Please try again.');
    }
  }

  private showSuccessNotification(message: string): void {
    this.snackBar.open(message, 'Close', {duration: 3000});
  }

  private showErrorNotification(message: string): void {
    this.snackBar.open(message, 'Close', {duration: 5000});
  }

  editComment(comment: CommentWithReplies): void {
    this.editingCommentId = comment.id;
    this.editedContent = comment.content;
  }

  cancelEdit(): void {
    this.editingCommentId = -1;
    this.editedContent = '';
  }

  saveEdit(comment: CommentWithReplies): void {
    if (!this.editedContent.trim()) {
      this.showErrorNotification('Comment cannot be empty');
      return;
    }

    if (this.isEditingInProgress || this.isCheckingSpam) {
      return;
    }

    this.reviewContent(this.editedContent, this.updateComment.bind(this));
  }

  private updateComment(): void {
    this.isEditingInProgress = true;

    this.commentService.updateComment(this.editingCommentId, this.editedContent).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: () => {
        this.fetchComments();
        this.showSuccessNotification('Comment updated successfully');
        this.resetEditState();
      },
      error: (error) => {
        console.error('Error updating comment:', error);
        this.isEditingInProgress = false;
        this.showErrorNotification('Error updating comment. Please try again.');
      }
    });
  }

  private resetEditState(): void {
    this.editingCommentId = -1;
    this.editedContent = '';
    this.isEditingInProgress = false;
  }

  deleteComment(id: number): void {
    this.commentService.deleteComment(id).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: () => {
        this.fetchComments();
        this.showSuccessNotification('Comment deleted successfully');
      },
      error: (error) => {
        console.error('Error deleting comment:', error);
        this.showErrorNotification('Error deleting comment. Please try again.');
      }
    });
  }

  toggleReplies(comment: CommentWithReplies): void {
    comment.showReplies = !comment.showReplies;
    if (comment.showReplies && (!comment.replies || comment.replies.length === 0)) {
      this.loadReplies(comment);
    }
  }

  loadReplies(comment: CommentWithReplies): void {
    this.replyService.getRepliesByCommentId(comment.id).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (replies) => {
        comment.replies = replies;
      },
      error: (error) => {
        console.error('Error fetching replies:', error);
        this.showErrorNotification('Failed to load replies. Please try again.');
      }
    });
  }

  toggleReply(comment: CommentWithReplies): void {
    this.comments$.pipe(
      take(1),
      takeUntil(this.destroy$)
    ).subscribe(comments => {
      comments.forEach(c => {
        if (c.id !== comment.id) {
          c.isReplyingTo = false;
        }
      });
    });
    comment.isReplyingTo = !comment.isReplyingTo;
  }

  cancelReply(comment: CommentWithReplies): void {
    comment.isReplyingTo = false;
  }

  onReplyAdded(comment: CommentWithReplies): void {
    comment.isReplyingTo = false;
    this.loadReplies(comment);
    comment.showReplies = true;
  }

  onReplyUpdated(comment: CommentWithReplies): void {
    this.loadReplies(comment);
  }

  onReplyDeleted(comment: CommentWithReplies, replyId: number): void {
    if (comment.replies) {
      comment.replies = comment.replies.filter(reply => reply.id !== replyId);
    }
  }
}
