import {Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {AuthService} from '../../../services/AuthService';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.css'
})
export class VerifyEmailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);

  status: 'loading' | 'success' | 'error' = 'loading';
  errorMessage: string | null = null;

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.status = 'error';
      this.errorMessage = 'Invalid verification link.';
      return;
    }

    this.authService.verifyEmail(token).subscribe({
      next: () => {
        this.status = 'success';
      },
      error: (error) => {
        this.status = 'error';
        this.errorMessage =
          (typeof error.error === 'string' ? error.error : error.error?.message) ||
          'This verification link is invalid or has expired.';
      }
    });
  }
}