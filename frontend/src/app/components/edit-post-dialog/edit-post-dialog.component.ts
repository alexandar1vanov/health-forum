import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {ForumPost} from '../../models/ForumPost';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButton} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatIcon} from '@angular/material/icon';
import {of} from 'rxjs';
import {catchError, debounceTime, switchMap, finalize} from 'rxjs/operators';
import {CommonModule} from '@angular/common';
import {GeminiAiService} from '../../services/GeminiAiService';


@Component({
  selector: 'app-edit-post-dialog',
  templateUrl: './edit-post-dialog.component.html',
  standalone: true,
  imports: [
    CommonModule,
    MatFormField,
    MatLabel,
    MatError,
    MatInput,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    ReactiveFormsModule,
    MatProgressSpinner,
    MatIcon,
    MatFormFieldModule
  ],
  styleUrls: ['./edit-post-dialog.component.css']
})
export class EditPostDialogComponent implements OnInit {
  editForm: FormGroup;
  isChecking = false;
  spamCheckResult: { isSpam: boolean, isRelevant: boolean, reason?: string } | null = null;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<EditPostDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { post: ForumPost },
    private geminiService: GeminiAiService,
    private snackBar: MatSnackBar
  ) {
    this.editForm = this.fb.group({
      title: [
        data.post.title,
        [Validators.required, Validators.minLength(5), Validators.maxLength(30)]
      ],
      content: [
        data.post.content,
        [Validators.required, Validators.minLength(10)]
      ]
    });
  }

  ngOnInit(): void {
    this.editForm.get('content')?.valueChanges.pipe(
      debounceTime(1250),
      switchMap(content => {
        if (!content || content.length < 10) {
          this.spamCheckResult = null;
          return of(null);
        }

        const title = this.editForm.get('title')?.value;
        this.isChecking = true;
        return this.geminiService.reviewForumPost(title, content, 'health forum post').pipe(
          finalize(() => this.isChecking = false),
          catchError(err => {
            this.spamCheckResult = null;
            console.error('Error checking for spam:', err);
            return of(null);
          })
        );
      })
    ).subscribe(result => {
      if (result) {
        console.log('Gemini review result for edit:', result);
        if (result.reason) {
          const lowerReason = result.reason.toLowerCase();
          const positiveIndicators = [
            'relevant to health',
            'health concern',
            'health-related',
            'no inappropriate',
            'appropriate',
            'not spam',
            'allowed'
          ];
          if (result.isSpam &&
            positiveIndicators.some(term => lowerReason.includes(term)) &&
            !lowerReason.includes("inappropriate") &&
            !lowerReason.includes("offensive")) {
            console.log('Overriding spam detection locally in edit component');
            result.isSpam = false;
          }
        }

        this.spamCheckResult = result;

        if (result.isSpam) {
          this.snackBar.open(`Potential spam detected: ${result.reason}`, 'Dismiss', {
            duration: 5000
          });
        }
      }
    });
    this.editForm.get('title')?.valueChanges.pipe(
      debounceTime(1250),
      switchMap(title => {
        if (!title || title.length < 5) {
          return of(null);
        }

        const content = this.editForm.get('content')?.value;
        if (!content) return of(null);

        this.isChecking = true;
        return this.geminiService.reviewForumPost(title, content, 'health forum post').pipe(
          finalize(() => this.isChecking = false),
          catchError(err => {
            console.error('Error checking title for spam:', err);
            return of(null);
          })
        );
      })
    ).subscribe(result => {
      if (result) {
        if (result.reason) {
          const lowerReason = result.reason.toLowerCase();
          const positiveIndicators = [
            'relevant to health',
            'health concern',
            'health-related',
            'no inappropriate',
            'appropriate'
          ];

          if (result.isSpam &&
            positiveIndicators.some(term => lowerReason.includes(term)) &&
            !lowerReason.includes("inappropriate") &&
            !lowerReason.includes("offensive")) {
            result.isSpam = false;
          }
        }

        this.spamCheckResult = result;

        if (result.isSpam) {
          this.snackBar.open(`Potential spam detected: ${result.reason}`, 'Dismiss', {
            duration: 5000
          });
        }
      }
    });
  }

  onSubmit(): void {
    if (this.editForm.valid) {
      const title = this.editForm.value.title;
      const content = this.editForm.value.content;

      this.isChecking = true;
      this.geminiService.reviewForumPost(title, content, 'health forum post')
        .pipe(finalize(() => this.isChecking = false))
        .subscribe({
          next: (result) => {
            console.log('Final submission check result:', result);
            if (result.reason) {
              const lowerReason = result.reason.toLowerCase();
              const positiveIndicators = [
                'relevant to health',
                'health concern',
                'health-related',
                'no inappropriate',
                'appropriate',
                'not spam',
                'allowed'
              ];

              if (result.isSpam &&
                positiveIndicators.some(term => lowerReason.includes(term)) &&
                !lowerReason.includes("inappropriate") &&
                !lowerReason.includes("offensive")) {
                result.isSpam = false;
              }
            }
            if (result.isSpam) {
              this.snackBar.open(`Cannot save changes: ${result.reason}`, 'Close', {
                duration: 7000
              });
              return;
            }
            this.dialogRef.close({
              id: this.data.post.id,
              title: title,
              content: content
            });
          },
          error: (error) => {
            console.error('Error in final content check:', error);
            const offensiveWords = ['fuck', 'shit', 'dick', 'ass', 'bitch', 'cunt', 'pussy'];
            const lowerTitle = title.toLowerCase();
            const lowerContent = content.toLowerCase();

            const hasOffensiveWords = offensiveWords.some(word =>
              lowerTitle.includes(word) || lowerContent.includes(word)
            );

            if (hasOffensiveWords) {
              this.snackBar.open('Your post contains inappropriate language. Please revise it.', 'Close', {
                duration: 5000
              });
              return;
            }
            this.dialogRef.close({
              id: this.data.post.id,
              title: title,
              content: content
            });
          }
        });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
