import {Component, inject, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {LikeResponse, PostLikeService} from '../../services/post-like.service';
import {AuthService} from '../../services/AuthService';

@Component({
  selector: 'app-post-like',
  standalone: true,
  templateUrl: './post-like.component.html',
  imports: [
    MatIcon
  ],
  styleUrls: ['./post-like.component.css']
})
export class PostLikeComponent implements OnInit, OnChanges {
  @Input() postId!: number;

  likeCount: number = 0;
  isLiked: boolean = false;
  isLoggedIn: boolean = false;
  userId: number | null = null;

  postLikeService = inject(PostLikeService);
  authService = inject(AuthService);

  ngOnInit(): void {
    this.userId = this.authService.getLoggedInUserId();
    this.isLoggedIn = this.userId !== null;
    this.loadLikeInfo();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['postId'] && !changes['postId'].firstChange) {
      console.log('Post ID changed to:', this.postId);
      this.loadLikeInfo();
    }
  }

  loadLikeInfo(): void {
    if (!this.postId) {
      console.error('Post ID is undefined');
      return;
    }

    console.log('Loading like info for post:', this.postId, 'and user:', this.userId);

    this.postLikeService.getLikeInfo(this.postId, this.userId || undefined)
      .subscribe({
        next: (response: LikeResponse) => {
          console.log('Received like info:', response);
          this.likeCount = response.likeCount;
          this.isLiked = response.isLiked;
          console.log('After setting, isLiked =', this.isLiked);
        },
        error: (error) => {
          console.error('Error loading like info:', error);
        }
      });
  }

  toggleLike(): void {
    if (!this.isLoggedIn || !this.userId) {
      return;
    }

    this.isLiked = !this.isLiked;
    this.likeCount += this.isLiked ? 1 : -1;

    this.postLikeService.toggleLike(this.postId, this.userId).subscribe({
      next: (response: LikeResponse) => {
        this.likeCount = response.likeCount;
        this.isLiked = response.isLiked;
      },
      error: () => {
        this.isLiked = !this.isLiked;
        this.likeCount += this.isLiked ? 1 : -1;
      }
    });
  }
}
