import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Disease} from '../models/Disease';
@Injectable({
  providedIn: 'root',
})
export class UserDiseaseService{
  private readonly saveUserDiseaseUrl = 'http://localhost:8080/api/user-diseases';
  private readonly userDiseasesUrl = 'http://localhost:8080/api/user-diseases/user'

  http = inject(HttpClient);
  private selectedDiseases: number[] = [];

  getSelectedDiseases() : number[] {
    return [...this.selectedDiseases]
  }
  hasSelectedDiseases(): boolean {
    return this.selectedDiseases.length > 0;
  }
  toggleDisease(diseaseId: number): void {
    const index = this.selectedDiseases.indexOf(diseaseId);
    if (index > -1) {
      this.selectedDiseases.splice(index, 1);
    } else {
      this.selectedDiseases.push(diseaseId);
    }
  }
  isSelected(diseaseId: number): boolean {
    return this.selectedDiseases.includes(diseaseId);
  }

  clearSelection(): void {
    this.selectedDiseases = [];
  }

  saveUserDiseases(userId: number, diseaseIds: number[]){
    return this.http.post<any[]>(`${this.saveUserDiseaseUrl}/${userId}`, diseaseIds)
  }

  getUserDiseases(userId: number) {
    return this.http.get<Disease[]>(`${this.userDiseasesUrl}/${userId}`)
  }

}

