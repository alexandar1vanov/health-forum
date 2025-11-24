import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {User} from '../models/User';
import {Role} from '../models/enums/Role';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly apiUserUrl = 'http://localhost:8080/api/admin'

  http = inject(HttpClient)

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUserUrl}/users`);
  }

  getSearchedUser(email: string): Observable<User> {
    return this.http.get<User>(`${this.apiUserUrl}/searched`, {
      params: new HttpParams().set('email', email)
    });
  }

  updateUser(id: number, updateData: {email?: string, role?: Role}): Observable<User> {
    return this.http.put<User>(`${this.apiUserUrl}/user/${id}`, {
      email: updateData.email,
      role: updateData.role
    });
  }

  deleteUser(userId: number): Observable<void> {
    if (!userId) {
      return throwError(() => new Error('Invalid user ID'));
    }
    return this.http.delete<void>(`${this.apiUserUrl}/user/${userId}`);
  }
}
