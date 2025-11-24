import { Component, inject, OnInit } from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import { Disease } from '../../models/Disease';
import { UserDiseaseService } from '../../services/UserDiseaseService';
import { AuthService } from '../../services/AuthService';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit {

  userDiseaseService = inject(UserDiseaseService);
  authService = inject(AuthService);
  router = inject(Router);

  diseasesByUser: Disease[] = [];
  isLoading: boolean = false;
  errorMessage: string | null = null;

  userEmail: string = ''

  ngOnInit(): void {
    const email = this.authService.getLoggedInUserEmail();
    if (email) {
      this.userEmail = email;
    }
    this.fetchDiseases();
  }

  fetchDiseases(): void {
    this.isLoading = true;
    this.errorMessage = null;

    const userId = this.authService.getLoggedInUserId();

    if (!userId) {
      this.errorMessage = 'Could not retrieve user';
      this.isLoading = false;
      return;
    }

    this.userDiseaseService.getUserDiseases(userId).subscribe({
      next: (data) => {
        this.diseasesByUser = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.handleDiseaseByUserFetchError(error);
      }
    });
  }

  private handleDiseaseByUserFetchError(error: any): void {
    console.error('Error fetching diseases:', error);
    this.errorMessage = 'Failed to load diseases.';
    this.isLoading = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
