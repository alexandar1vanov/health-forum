import { Component } from '@angular/core';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {RouterOutlet} from '@angular/router';
import {ForumPostComponent} from '../forum-post/forum-post.component';
import {UserPostsComponent} from '../user-posts/user-posts.component';

@Component({
  selector: 'app-homepage',
  imports: [
    SidebarComponent,
    RouterOutlet,
    ForumPostComponent,
    UserPostsComponent
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent {

}
