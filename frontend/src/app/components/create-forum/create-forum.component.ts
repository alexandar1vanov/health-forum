import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Disease } from '../../models/Disease';
import { ForumPostService } from '../../services/forum-post.service';
import { AuthService } from '../../services/AuthService';
import { DiseaseService } from '../../services/DiseaseService';
import { Router, RouterLink } from '@angular/router';
import { CreateForum } from '../../models/CreateForum';
import { GeminiAiService } from '../../services/GeminiAiService';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-create-forum',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    FormsModule,
    RouterLink,
  ],
  templateUrl: './create-forum.component.html',
  styleUrls: ['./create-forum.component.css']
})
export class CreateForumComponent implements OnInit {

  private forumService = inject(ForumPostService);
  private authService = inject(AuthService);
  private diseaseService = inject(DiseaseService);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private geminiService = inject(GeminiAiService);
  private snackBar = inject(MatSnackBar);

  createForumPostForm!: FormGroup;
  allDiseases: Disease[] = [];
  isSubmitted: boolean = false;
  errorMessage: string = '';
  searchTerm: string = '';
  filteredDiseases: Disease[] = [];
  selectedDiseaseIds: number[] = [];

  isCheckingSpam: boolean = false;

  ngOnInit(): void {
    this.initializeForm();
    this.loadDiseases();
  }

  initializeForm(): void {
    this.createForumPostForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(30)]],
      content: ['', [Validators.required, Validators.maxLength(1000)]]
    });
  }

  loadDiseases(): void {
    this.diseaseService.getAllDiseases().subscribe({
      next: (data) => {
        this.allDiseases = data;
        this.filteredDiseases = data;
      },
      error: (error) => {
        console.error('Error loading diseases', error);
        this.errorMessage = 'Failed to load diseases. Please try again later.';
      }
    });
  }

  filterDiseases(): void {
    if (!this.searchTerm.trim()) {
      this.filteredDiseases = this.allDiseases;
      return;
    }
    this.filteredDiseases = this.allDiseases.filter(disease =>
      disease.name.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  toggleDiseaseSelection(diseaseId: number): void {
    const index = this.selectedDiseaseIds.indexOf(diseaseId);
    if (index > -1) {
      this.selectedDiseaseIds.splice(index, 1);
    } else {
      this.selectedDiseaseIds.push(diseaseId);
    }
  }

  isDiseaseSelected(diseaseId: number): boolean {
    return this.selectedDiseaseIds.includes(diseaseId);
  }

  getDiseaseNameById(diseaseId: number): string {
    const disease = this.allDiseases.find(d => d.id === diseaseId);
    return disease ? disease.name : '';
  }

  onSubmit(): void {
    this.errorMessage = '';

    if (this.createForumPostForm.invalid) {
      this.markFormGroupTouched(this.createForumPostForm);
      this.errorMessage = 'Please fill out all required fields correctly.';
      return;
    }

    const userId = this.authService.getLoggedInUserId();
    if (!userId) {
      this.errorMessage = 'You must be logged in to create a post';
      return;
    }

    if (this.selectedDiseaseIds.length === 0) {
      this.errorMessage = 'Please select at least one relevant disease.';
      return;
    }

    const title = this.createForumPostForm.value.title.trim();
    const content = this.createForumPostForm.value.content.trim();

    this.checkForSpam(title, content);
  }

  private checkForSpam(title: string, content: string): void {
    if (this.isCheckingSpam || this.isSubmitted) {
      return;
    }

    this.isCheckingSpam = true;

    const diseaseNames = this.selectedDiseaseIds.map(id => this.getDiseaseNameById(id)).join(', ');
    const forumContext = `health forum post about ${diseaseNames}`;

    this.geminiService.reviewForumPost(title, content, forumContext)
      .pipe(finalize(() => this.isCheckingSpam = false))
      .subscribe({
        next: (result) => {
          console.log('Gemini review result:', result);

          if (result.isSpam) {
            this.errorMessage = `Your post has been flagged: ${result.reason || 'inappropriate content'}`;
            this.snackBar.open(
              `Your post has been flagged: ${result.reason || 'inappropriate content'}`,
              'Close',
              {duration: 8000, panelClass: 'warning-snackbar'}
            );

            if (result.reason && result.reason.toLowerCase().includes('title')) {
              this.titleControl?.setErrors({'inappropriate': true});
              this.createForumPostForm.markAllAsTouched();
            }
            return;
          }

          if (!result.isRelevant) {
            this.errorMessage = `Your post appears to be off-topic: ${result.reason || 'not relevant to health topics'}`;
            this.snackBar.open(
              `Your post appears to be off-topic: ${result.reason || 'not relevant to health topics'}`,
              'Close',
              {duration: 8000, panelClass: 'warning-snackbar'}
            );
            return;
          }
          this.submitPost(title, content);
        },
        error: (error) => {
          console.error('Error reviewing post:', error);
          const offensiveWords = ['fuck', 'shit', 'dick', 'ass', 'bitch', 'cunt', 'pussy'];
          const lowerTitle = title.toLowerCase();
          const lowerContent = content.toLowerCase();

          const hasOffensiveWords = offensiveWords.some(word =>
            lowerTitle.includes(word) || lowerContent.includes(word)
          );

          if (hasOffensiveWords) {
            this.errorMessage = 'Your post contains inappropriate language. Please revise it.';
            this.snackBar.open(
              'Your post contains inappropriate language. Please revise it.',
              'Close',
              {duration: 5000, panelClass: 'warning-snackbar'}
            );
            return;
          }

          this.snackBar.open(
            'We encountered an issue reviewing your post. Proceeding with submission.',
            'Close',
            {duration: 5000}
          );
          this.submitPost(title, content);
        }
      });
  }

  private submitPost(title: string, content: string): void {
    const userId = this.authService.getLoggedInUserId();

    const postData: CreateForum = {
      title: title,
      content: content,
      userId: userId!,
      diseaseIds: this.selectedDiseaseIds
    };

    this.isSubmitted = true;
    this.forumService.createForumPost(postData).subscribe({
      next: () => {
        this.isSubmitted = false;
        this.selectedDiseaseIds = [];
        this.snackBar.open('Post created successfully', 'Close', {duration: 3000});
        this.router.navigate(['/home']);
      },
      error: (error) => {
        this.isSubmitted = false;
        console.error('Error creating post:', error);
        this.errorMessage = error.message || 'Failed to create post. Please try again later.';
        this.snackBar.open(
          'Error creating post. Please try again.',
          'Close',
          {duration: 5000}
        );
      }
    });
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  get titleControl() { return this.createForumPostForm.get('title'); }
  get contentControl() { return this.createForumPostForm.get('content'); }
}
