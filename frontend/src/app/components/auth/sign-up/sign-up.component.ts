import {Component, inject} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {AuthService} from '../../../services/AuthService';
import {CommonModule} from '@angular/common';
import {SignUpDTO} from '../../../models/SignUpDTO';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    RouterLink,
    RouterLinkActive
  ],
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  isLoading = false;
  errorMessage: string | null = null;

  signupForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [
      Validators.required,
      Validators.minLength(8),
      Validators.pattern(/^(?=.*[A-Z])(?=\S+$).{8,}$/)
    ]],
  });

  onSubmit(): void {
    if (this.signupForm.invalid || this.isLoading) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    const signupData: SignUpDTO = {
      email: this.signupForm.value.email!,
      password: this.signupForm.value.password!
    };

    this.authService.registerUser(signupData).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/login']);
        console.log("Registration successful");
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
        console.error('Registration error:', error);
      }
    });
  }
}
