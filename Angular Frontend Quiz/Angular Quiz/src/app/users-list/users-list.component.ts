import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';

import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { Store } from '@ngrx/store';
import * as fromApp from '../app.reducer';
import * as UsersActions from '../store/users.actions';
import { User } from '../models/user';
import { CommonModule } from '@angular/common';
import { Subscription, take } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { animate, state, style, transition, trigger } from '@angular/animations';



@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    MatPaginatorModule,
    MatTableModule,
    MatSortModule,
    MatInputModule,
    MatProgressBarModule,
    CommonModule,
    FormsModule,
  ],
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss'],
  animations: [
    trigger('moveFromBottom', [
      state('hidden', style({ opacity: 0, transform: 'translateY(150px)' })),
      state('visible', style({ opacity: 1, transform: 'translateY(0)' })),
      transition('hidden => visible', animate('1s ease-out'))
    ]),
  ],
})
export class UsersListComponent implements OnInit {

  isVisible = false;
  isLoading: boolean = true;

  dataSource = new MatTableDataSource<User>([]);
  displayedColumns = ['avatar', 'first_name', 'last_name', 'id'];
  UsersCount: number = 0;
  pageSize: number = 6;

  selectedUserId?: number;

  pageIndex = 0;

  subscription?: Subscription;

  constructor(
    private store: Store<fromApp.AppState>,
    private router: Router) { }


  ngOnInit(): void {

    // We dispatch a fetch action the 1st time we open the app to the fill the store
    this.store.select('usersReducer')
      .pipe(take(1))
      .subscribe(state => {
        if (state.users.length === 0) {
          this.store.dispatch(UsersActions.UsersFetchStart({ pageIndex: 1 }));
        } else { // to redisplay our table when we go back to the userslist
          setTimeout(() => {
            this.isVisible = true;
          }, 1); // Ensures the animation triggers
        }
      });


    this.subscription = this.store.select('usersReducer')
      .subscribe(usersState => {
        // If we are searching for a specific user by ID, we display only the user
        // We have searchingForUser and selectedUser variables in store to save this searched / selected user and not 
        // overwrite the whole users list
        // When we remove the ID to stop searching, we display back the users that are in store,

        //This case is for when we don't find a user with this id.
        if (usersState.searchingForUser && usersState.selectedUser === null) {
          this.pageIndex = 0;
          this.pageSize = 1;
          this.UsersCount = 0;
          this.dataSource = new MatTableDataSource();
        }
        //This case is for when we do find a user with this id.
        else if (usersState.searchingForUser && usersState.selectedUser !== null) {
          this.pageIndex = 0;
          this.pageSize = 1;
          this.UsersCount = 1;
          this.selectedUserId = usersState.selectedUser!.id;
          this.dataSource = new MatTableDataSource([usersState.selectedUser!]);
        }
        //This case is for fetching the whole users list
        else {
          this.pageIndex = usersState.pageIndex - 1;
          this.pageSize = usersState.users.length;
          this.UsersCount = usersState.usersTotal;
          this.dataSource = new MatTableDataSource(usersState.users);
        }

        this.isLoading = usersState.loading;
        if (this.isLoading && !this.isVisible) {
          setTimeout(() => {
            this.isVisible = true;
          }, 1100); // Ensures the animation triggers
        }
      });
  }

  onPageChange(event: PageEvent) { // fetches the next or the previous page of the pagination

    // We check for undefined since at start we will be at the first page so there is nothing previous to it
    if (event.previousPageIndex === undefined) {
      return;
    }
    // We add 1 always because the mat-paginator pageIndex starts at 0, but the dummy data pageIndex starts at 1 
    else if (event.pageIndex > event.previousPageIndex) {
      this.pageIndex++;
      this.store.dispatch(UsersActions.UsersFetchStart({ pageIndex: this.pageIndex + 1 }));
    } else {
      this.pageIndex--;
      this.store.dispatch(UsersActions.UsersFetchStart({ pageIndex: this.pageIndex + 1 }));
    }

  }

  SearchUsersById(id: KeyboardEvent) {

    const Id = (id.target as HTMLInputElement).value;

    // We only take postive IDs, we can't accept negative numbers
    if (+Id > 0) {
      this.store.dispatch(UsersActions.UserFetchByIdStart({ userId: +Id, searchingForUser: true }));
    }
    // When there is no input, it means the client wants to display the whole users list so we delete the selectedUser
    else if (+Id === 0) {
      this.store.dispatch(UsersActions.DeleteSelectedUser());
    }
  }

  ShowUserInfo(Id: number) { // navigates to the userdetails component
    this.router.navigate(['userdetails/' + Id])
  }

  ngOnDestroy(): void { // cleanUp
    this.subscription?.unsubscribe();
  }
}

