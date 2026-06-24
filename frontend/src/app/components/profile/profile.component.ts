import {Component, inject, OnInit} from '@angular/core';
import {DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {UserService} from '../../services/UserService';
import {UserDiseaseService} from '../../services/UserDiseaseService';
import {DiseaseService} from '../../services/DiseaseService';
import {AuthService} from '../../services/AuthService';
import {ProfileResponse} from '../../models/ProfileResponse';
import {DiseaseResponse} from '../../models/DiseaseResponse';
import {SidebarComponent} from '../sidebar/sidebar.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [DatePipe, FormsModule, SidebarComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  private userService = inject(UserService);
  private userDiseaseService = inject(UserDiseaseService);
  private diseaseService = inject(DiseaseService);
  private authService = inject(AuthService);

  profile: ProfileResponse | null = null;
  userDiseases: DiseaseResponse[] = [];
  allDiseases: DiseaseResponse[] = [];

  isLoading = false;
  errorMessage: string | null = null;

  // edit профил
  editMode = false;
  editName = '';
  editSurname = '';

  // add болест
  showAddDisease = false;
  selectedDiseaseId: number | null = null;

  ngOnInit(): void {
    this.fetchProfile();
    this.fetchUserDiseases();
    this.diseaseService.getAllDiseases().subscribe({
      next: (data) => this.allDiseases = data
    });
  }

  fetchProfile(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.userService.getProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching profile:', error);
        this.errorMessage = 'Failed to load profile.';
        this.isLoading = false;
      }
    });
  }

  fetchUserDiseases(): void {
    const userId = this.authService.getLoggedInUserId();
    if (!userId) return;
    this.userDiseaseService.getUserDiseases(userId).subscribe({
      next: (data) => this.userDiseases = data
    });
  }

  // ---- edit профил ----
  startEdit(): void {
    if (!this.profile) return;
    this.editName = this.profile.name ?? '';
    this.editSurname = this.profile.surname ?? '';
    this.editMode = true;
  }

  cancelEdit(): void {
    this.editMode = false;
  }

  saveProfile(): void {
    this.userService.updateProfile({name: this.editName, surname: this.editSurname}).subscribe({
      next: (data) => {
        this.profile = data;
        this.editMode = false;
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        this.errorMessage = 'Failed to update profile.';
      }
    });
  }

  // ---- add / delete болест ----
  get availableDiseases(): DiseaseResponse[] {
    const ownedIds = this.userDiseases.map(d => d.id);
    return this.allDiseases.filter(d => !ownedIds.includes(d.id));
  }

  toggleAddDisease(): void {
    this.showAddDisease = !this.showAddDisease;
    this.selectedDiseaseId = null;
  }

  addDisease(): void {
    if (!this.selectedDiseaseId) return;
    this.userService.addDiseases([this.selectedDiseaseId]).subscribe({
      next: () => {
        this.showAddDisease = false;
        this.selectedDiseaseId = null;
        this.fetchUserDiseases();
      },
      error: (error) => {
        console.error('Error adding disease:', error);
        this.errorMessage = 'Failed to add disease.';
      }
    });
  }

  removeDisease(diseaseId: number): void {
    this.userService.removeDisease(diseaseId).subscribe({
      next: () => this.fetchUserDiseases(),
      error: (error) => {
        console.error('Error removing disease:', error);
        this.errorMessage = 'Failed to remove disease.';
      }
    });
  }
}
