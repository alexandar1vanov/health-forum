import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginDTO } from '../models/LoginDTO';
import { SignUpDTO } from '../models/SignUpDTO';
import {JwtPayload} from '../models/token/JwtPayload';
import {jwtDecode} from 'jwt-decode';
import {TokenResponse} from '../models/TokenResponse';
import { UserDiseaseService } from './UserDiseaseService';
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly url = 'http://localhost:8080';
  http = inject(HttpClient);
  private userDiseaseService = inject(UserDiseaseService);

  private currentRole = new BehaviorSubject<string | null>(null);
  private hasSelectedDiseasesSubject = new BehaviorSubject<boolean>(false);

  constructor() {
    const hasSelectedDiseases = localStorage.getItem('hasSelectedDiseases');
    const token = this.getToken()
    if (hasSelectedDiseases) {
      this.hasSelectedDiseasesSubject.next(hasSelectedDiseases === 'true');
    }
    if (token){
      this.decodeAndStoreRole(token)
    }
  }

  private decodeAndStoreRole(token: string) {
    try {
      const decoded = jwtDecode<JwtPayload>(token)
      this.currentRole.next(decoded.role || null)
    }catch (error) {
      this.currentRole.next(null)
    }
  }
  getCurrentRole(): Observable<string | null> {
    return this.currentRole.asObservable()
  }
  isAdmin(): boolean {
    const role = this.currentRole.value
    return role === 'ADMIN'
  }
  isUser(): boolean {
    const role = this.currentRole.value
    return role === 'USER'
  }

  login(loginData: LoginDTO): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.url}/login`, loginData)
      .pipe(
        tap(response => {
          localStorage.setItem('token', response.token);
          localStorage.setItem('hasSelectedDiseases', String(response.selectedDiseases));
          this.hasSelectedDiseasesSubject.next(response.selectedDiseases);
          this.decodeAndStoreRole(response.token);
          this.userDiseaseService.clearSelection()
        })
      );
  }

  registerUser(user: SignUpDTO) {
    return this.http.post(`${this.url}/signup`, user, {responseType: "text"});
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('hasSelectedDiseases');
    this.currentRole.next(null);
    this.userDiseaseService.clearSelection()
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  hasSelectedDiseases(): boolean {
    return this.hasSelectedDiseasesSubject.value;
  }

  updateDiseaseSelection(selected: boolean): void {
    localStorage.setItem('hasSelectedDiseases', String(selected));
    this.hasSelectedDiseasesSubject.next(selected);
  }

  getLoggedInUserId(): number | null {
    const token = this.getToken();
    if (token) {
      try {
        const decodedToken = jwtDecode<JwtPayload>(token);
        if (decodedToken?.userId !== undefined) {
          return decodedToken.userId;
        } else if (typeof decodedToken?.sub === 'string') {
          const parsedSub = parseInt(decodedToken.sub, 10);
          return isNaN(parsedSub) ? null : parsedSub;
        } else if (typeof decodedToken?.sub === 'number') {
          return decodedToken.sub;
        } else {
          return null;
        }
      } catch (error) {
        console.error('Error decoding token:', error);
        return null;
      }
    }
    return null;
  }
  getLoggedInUserEmail(): string | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const decodedToken = jwtDecode<JwtPayload & { email?: string }>(token);
      return decodedToken?.email ||
        decodedToken?.email ||
        (typeof decodedToken?.sub === 'string' && decodedToken.sub.includes('@') ? decodedToken.sub : null);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

}
