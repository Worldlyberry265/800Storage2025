import { Routes } from '@angular/router';
import { UsersListComponent } from './users-list/users-list.component';
import { UserdetailsComponent } from './userdetails/userdetails.component';
import { userResolver } from './userdetails/user-resolver';

export const routes: Routes = [

  { path: 'userslist', component: UsersListComponent },
  { path: 'userdetails/:id', component: UserdetailsComponent, resolve: { user: userResolver } },
  // Everything else will be redirected to userslist route
  { path: '**' , redirectTo: 'userslist'}
];

export class AppRoutingModule { }
