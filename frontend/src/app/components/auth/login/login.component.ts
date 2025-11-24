import {Component, inject} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../services/AuthService';
import {Router, RouterLink} from '@angular/router';
import {catchError, finalize, tap, throwError} from 'rxjs';
import {LoginDTO} from '../../../models/LoginDTO';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  isLoading = false;
  errorMessage: string | null = null;

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  })

  onSubmit(): void {
    if (this.loginForm.invalid || this.isLoading) return;

    this.isLoading = true;
    this.errorMessage = null;

    const formValues = this.loginForm.getRawValue();

    const loginData: LoginDTO = {
      email: formValues.email,
      password: formValues.password,
    };

    this.authService.login(loginData).pipe(
      tap((response) => {
        if (response.selectedDiseases) {
          this.router.navigate(['/home']);
        } else {
          this.router.navigate(['/select-diseases']);
        }
        console.log('Login successful!');
        console.log('Token received:', this.authService.getToken())
      }),
      catchError(err => {
        this.errorMessage = err.error?.message || 'Incorrect email or password';
        return throwError(() => err);
      }),
      finalize(() => this.isLoading = false)
    ).subscribe();
  }

}
