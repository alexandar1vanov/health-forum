import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {Disease} from '../../models/Disease';
import {AuthService} from '../../services/AuthService';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {UserDiseaseService} from '../../services/UserDiseaseService';
import {DiseaseService} from '../../services/DiseaseService';
import {debounceTime, distinctUntilChanged, Subject, takeUntil} from 'rxjs';
@Component({
  selector: 'app-select-diseases',
  imports: [FormsModule],
  templateUrl: './select-diseases.component.html',
  styleUrl: './select-diseases.component.css'
})
export class SelectDiseasesComponent implements OnInit, OnDestroy {

  private readonly diseaseService = inject(DiseaseService)
  readonly userDiseaseService = inject(UserDiseaseService)
  private readonly authService = inject(AuthService)
  private readonly router = inject(Router)

  diseases: Disease[] = []
  filteredDiseases: Disease[] = []
  searchTerm: string = '';

  isLoading: boolean = false
  errorMessage: string | null = null
  saveSuccessful: boolean = false;

  private searchTerms = new Subject<string>()
  private destroy$ = new Subject<void>();


  ngOnInit(): void {
    this.setupSearch()
    this.fetchDiseases()
  }

  ngOnDestroy(): void {
    this.destroy$.next()
    this.destroy$.complete()
    this.searchTerms.complete()
  }

  private setupSearch(): void {
    this.searchTerms.pipe(
      takeUntil(this.destroy$),
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(term => {
      this.filterDiseases(term)
    })
  }

  clearAllSelections(): void {
    this.userDiseaseService.clearSelection();
  }

  fetchDiseases(): void {
    this.isLoading = true
    this.errorMessage = null

    this.diseaseService.getAllDiseases()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
      next: data => {
        this.diseases = data
        this.filteredDiseases = [...data]
        this.isLoading = false
      },
      error: (error) => {
        this.handleDiseaseFetchError(error)
      }
    })
  }

  onSearchInput(): void {
    this.searchTerms.next(this.searchTerm);
  }

  private filterDiseases(term: string): void {
    if (!term.trim()) {
      this.filteredDiseases = [...this.diseases];
      return;
    }
    const lowerTerm = term.toLowerCase();
    this.filteredDiseases = this.diseases.filter(disease =>
      disease.name.toLowerCase().includes(lowerTerm)
    );
  }

  private handleDiseaseFetchError(error: any): void {
    this.errorMessage = 'Failed to load diseases.';
    console.error('Error fetching diseases:', error);
    this.isLoading = false;

    if (error.status === 401 || error.status === 403) {
      this.router.navigate(['/login']);
    }
  }


  toggleDisease(diseaseId: number): void {
    this.userDiseaseService.toggleDisease(diseaseId);
  }

  saveSelection(): void {
    if (!this.userDiseaseService.hasSelectedDiseases()) {
      this.errorMessage = 'Please select at least one disease';
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.saveSuccessful = false;

    const userId = this.authService.getLoggedInUserId();

    if (!userId) {
      this.errorMessage = 'Could not retrieve user';
      this.isLoading = false;
      return;
    }

    const selectedDiseases = this.userDiseaseService.getSelectedDiseases();
    this.userDiseaseService.saveUserDiseases(userId, selectedDiseases)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
      next: () => this.handleSaveSuccess(),
      error: (error) => this.handleSaveError(error)
    });
  }

  private handleSaveSuccess(): void {
    this.authService.updateDiseaseSelection(true);
    this.saveSuccessful = true;
    setTimeout(() => {
      this.router.navigate(['/home']);
    }, 1500);
    this.isLoading = false;
  }

  private handleSaveError(error: any): void {
    this.errorMessage = 'Failed to save selected diseases';
    console.error('Error saving selections', error);
    this.isLoading = false;
  }
}
