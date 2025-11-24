import {Component, inject, OnInit} from '@angular/core';
import {User} from '../../models/User';
import {UserService} from '../../services/UserService';
import {DatePipe} from '@angular/common';
import {UserPostsComponent} from '../user-posts/user-posts.component';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {UserUpdateComponent} from '../user-update/user-update.component';

@Component({
  selector: 'app-admin-panel',
  imports: [
    DatePipe,
    UserPostsComponent,
    SidebarComponent,
    UserUpdateComponent,
  ],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.css'
})
export class AdminPanelComponent implements OnInit {

  userService = inject(UserService)

  users: User[] = []
  editingUser: any = null
  searchedUser: User | null = null
  isLoading: boolean = true
  errorMessage: string = ''

  ngOnInit(): void {
    this.loadUsers()
  }

  loadUsers(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.searchedUser = null;

    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load users';
        this.isLoading = false;
      }
    });
  }

  searchUser(email: string): void {
    if (!email || email.trim() === '') {
      this.loadUsers();
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';

    this.userService.getSearchedUser(email).subscribe({
      next: (data) => {
        this.searchedUser = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = `Failed to load user with Email '${email}'`;
        this.isLoading = false;
      },
    });
  }

  deleteUser(userId: number): void {
    if (!userId) {
      this.errorMessage = 'Invalid user ID';
      return;
    }
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(userId).subscribe({
        next: () => {
          this.users = this.users.filter(u => u.id !== userId);
        },
        error: (err) => console.error(err)
      });
    }
  }

  startEdit(user: any): void {
    this.editingUser = user;
  }

  updateUser(updatedData: any): void {
    this.userService.updateUser(this.editingUser.id, updatedData).subscribe({
      next: (updatedUser) => {
        const index = this.users.findIndex(u => u.id === updatedUser.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }
        this.editingUser = null;
        this.loadUsers()
      },
      error: (err) => {
        console.error('Error updating user:', err);
      }
    });
  }

  cancelEdit(): void {
    this.editingUser = null;
  }
}
