import { Routes } from '@angular/router';
import {LoginComponent} from './components/auth/login/login.component';
import {SignUpComponent} from './components/auth/sign-up/sign-up.component';
import {HomepageComponent} from './components/homepage/homepage.component';
import {SelectDiseasesComponent} from './components/select-diseases/select-diseases.component';
import {CreateForumComponent} from './components/create-forum/create-forum.component';
import {ForumPostDetailsComponent} from './components/forum-post-details/forum-post-details.component';
import { AdminPanelComponent } from './components/admin-panel/admin-panel.component';
import {AuthGuard} from './routes-guard/AuthGuard';
import {RoleGuard} from './routes-guard/RoleGuard';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: '', redirectTo: 'signup', pathMatch: 'full'},

  {path: 'signup', component: SignUpComponent},

  {path: 'home', component: HomepageComponent, canActivate: [AuthGuard]},

  {path: 'diseasePosts/:id', component: HomepageComponent, canActivate: [AuthGuard]},

  {path: 'userPost/:id', component: ForumPostDetailsComponent, canActivate: [AuthGuard]},

  {path: 'post/:id', component: ForumPostDetailsComponent, canActivate: [AuthGuard]},

  {path: 'select-diseases', component: SelectDiseasesComponent, canActivate: [AuthGuard]},

  {path: 'create-forum', component: CreateForumComponent, canActivate: [AuthGuard]},

  {path: 'admin-panel', component: AdminPanelComponent, canActivate: [AuthGuard, RoleGuard],data: {roles: ['ADMIN']}},
];
