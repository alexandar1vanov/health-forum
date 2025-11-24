import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild, inject } from '@angular/core';
import { AsyncPipe, DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Reply } from '../../models/Reply';
import { ReplyService } from '../../services/reply.service';
import { GeminiAiService } from '../../services/GeminiAiService';
import { AuthService } from '../../services/AuthService';
import { CreateReply } from '../../models/CreateReply';

@Component({
  selector: 'app-reply',
  standalone: true,
  imports: [
    MatIcon,
    DatePipe,
    ReactiveFormsModule,
    FormsModule,
    MatIconButton,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
  ],
  templateUrl: './reply.component.html',
  styleUrls: ['./reply.component.css']
})
export class ReplyComponent implements OnInit {
  @Input() reply: Reply | null = null;
  @Input() commentId!: number;
  @Input() postTitle: string = '';
  @Input() isAddingNewReply: boolean = false;
  @Output() replyAdded = new EventEmitter<boolean>();
  @Output() replyCancelled = new EventEmitter<void>();
  @Output() replyUpdated = new EventEmitter<boolean>();

  @Output() replyDeleted = new EventEmitter<number>();

  @ViewChild('replyInput') replyInput!: ElementRef<HTMLTextAreaElement>;
  replyForm!: FormGroup;
  editingReplyId: number = -1;
  editedContent: string = '';
  isPosting: boolean = false;
  isCheckingSpam: boolean = false;
  isEditingInProgress: boolean = false;
  currUserId: number | null = null;

  currUserEmail: string | null = null;
  private formBuilder = inject(FormBuilder);
  private replyService = inject(ReplyService);
  private authService = inject(AuthService);
  private geminiService = inject(GeminiAiService);
  private snackBar = inject(MatSnackBar);

  ngOnInit(): void {
    this.currUserId = this.authService.getLoggedInUserId();
    this.currUserEmail = this.authService.getLoggedInUserEmail();
    this.initForm();

    if (this.isAddingNewReply) {
      setTimeout(() => {
        this.focusInput();
      }, 100);
    }
  }

  initForm(): void {
    this.replyForm = this.formBuilder.group({
      content: ['', [Validators.required, Validators.maxLength(500)]]
    });
  }

  focusInput(): void {
    if (this.replyInput && this.replyInput.nativeElement) {
      this.replyInput.nativeElement.focus();
    }
  }

  onBlur(): void {
    if (!this.replyForm.get('content')?.value?.trim()) {
      this.cancelReply();
    }
  }

  cancelReply(): void {
    this.replyCancelled.emit();
  }

  onSubmit(): void {
    if (this.replyForm.invalid || this.isPosting || this.isCheckingSpam) {
      return;
    }

    if (!this.commentId || !this.currUserId) {
      console.error('Cannot post reply: missing commentId or userId');
      return;
    }

    const content = this.replyForm.get('content')?.value?.trim();
    if (!content) {
      return;
    }

    this.isCheckingSpam = true;
    const postContext = `health forum comment reply about ${this.postTitle}`;

    this.geminiService.reviewContent(content, postContext).subscribe({
      next: (result) => {
        this.isCheckingSpam = false;
        console.log('Gemini review result:', result);

        if (result.isSpam) {
          this.snackBar.open(
            `Your reply has been flagged as potential spam: ${result.reason}`,
            'Close',
            { duration: 8000, panelClass: 'warning-snackbar' }
          );
          this.replyForm.get('content')?.setErrors({
            serverError: 'Reply has been flagged as spam. Please revise your content.'
          });
          return;
        }

        if (!result.isRelevant) {
          this.snackBar.open(
            `Your reply appears to be off-topic: ${result.reason}`,
            'Close',
            { duration: 8000, panelClass: 'warning-snackbar' }
          );
          this.replyForm.get('content')?.setErrors({
            serverError: 'Reply is not relevant to this discussion. Please ensure your reply is on-topic.'
          });
          return;
        }

        this.postReply(content);
      },
      error: (error) => {
        console.error('Error reviewing reply:', error);
        this.isCheckingSpam = false;
        this.snackBar.open(
          'We encountered an issue reviewing your reply. Please try again.',
          'Close',
          { duration: 5000 }
        );
      }
    });
  }

  private postReply(content: string): void {
    this.isPosting = true;

    const replyRequest: CreateReply = {
      content: content,
      userId: this.currUserId!,
      commentId: this.commentId
    };

    this.replyService.postReply(replyRequest)
      .subscribe({
        next: (response: Reply) => {
          this.replyForm.reset();
          this.isPosting = false;
          this.replyAdded.emit(true);

          this.snackBar.open(
            'Reply posted successfully',
            'Close',
            { duration: 3000 }
          );
        },
        error: (error) => {
          console.error('Error posting reply:', error);
          this.isPosting = false;
          if (error.status === 400) {
            const validationErrors = error.error;
            if (validationErrors.content) {
              this.replyForm.get('content')?.setErrors({
                serverError: validationErrors.content
              });
            }
          } else {
            this.snackBar.open(
              'Error posting reply. Please try again.',
              'Close',
              { duration: 5000 }
            );
          }
        }
      });
  }

  editReply(): void {
    if (!this.reply) return;
    this.editingReplyId = this.reply.id;
    this.editedContent = this.reply.content;
  }

  cancelEdit(): void {
    this.editingReplyId = -1;
    this.editedContent = '';
  }

  saveEdit(): void {
    if (!this.reply) return;

    if (!this.editedContent.trim()) {
      this.snackBar.open('Reply content cannot be empty', 'Close', { duration: 3000 });
      return;
    }

    if (this.isEditingInProgress || this.isCheckingSpam) {
      return;
    }

    this.isCheckingSpam = true;
    const postContext = `health forum comment reply about ${this.postTitle}`;

    this.geminiService.reviewContent(this.editedContent, postContext).subscribe({
      next: (result) => {
        this.isCheckingSpam = false;

        if (result.isSpam) {
          this.snackBar.open(
            `Your reply has been flagged as potential spam: ${result.reason}`,
            'Close',
            { duration: 8000, panelClass: 'warning-snackbar' }
          );
          return;
        }

        if (!result.isRelevant) {
          this.snackBar.open(
            `Your reply appears to be off-topic: ${result.reason}`,
            'Close',
            { duration: 8000, panelClass: 'warning-snackbar' }
          );
          return;
        }

        this.updateReplyContent();
      },
      error: (error) => {
        console.error('Error reviewing edited reply:', error);
        this.isCheckingSpam = false;
        this.snackBar.open(
          'We encountered an issue reviewing your reply. Please try again.',
          'Close',
          { duration: 5000 }
        );
      }
    });
  }

  private updateReplyContent(): void {
    if (!this.reply) return;

    this.isEditingInProgress = true;

    this.replyService.updateReply(this.reply.id, this.editedContent).subscribe({
      next: (response) => {
        this.isEditingInProgress = false;
        this.editingReplyId = -1;
        this.editedContent = '';
        this.replyUpdated.emit(true);
        this.snackBar.open('Reply updated successfully', 'Close', { duration: 3000 });
      },
      error: (error) => {
        this.isEditingInProgress = false;
        console.error('Error updating reply:', error);
        this.snackBar.open('Error updating reply. Please try again.', 'Close', { duration: 5000 });
      }
    });
  }

  deleteReply(): void {
    if (!this.reply) return;

    this.replyService.deleteReply(this.reply.id).subscribe({
      next: () => {
        this.replyDeleted.emit(this.reply!.id);
        this.snackBar.open('Reply deleted successfully', 'Close', { duration: 3000 });
      },
      error: (error) => {
        console.error('Error deleting reply:', error);
        this.snackBar.open('Error deleting reply. Please try again.', 'Close', { duration: 5000 });
      }
    });
  }
}
