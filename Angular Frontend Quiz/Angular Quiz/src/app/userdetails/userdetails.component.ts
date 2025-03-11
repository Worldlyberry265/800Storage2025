import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import * as fromApp from '../app.reducer';
import { Subscription } from 'rxjs';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { User } from '../models/user';

@Component({
  selector: 'app-userdetails',
  templateUrl: './userdetails.component.html',
  styleUrls: ['./userdetails.component.scss'],
  standalone: true,
  imports: [RouterModule, CommonModule]
})
export class UserdetailsComponent implements OnInit, OnDestroy {

  constructor(private store: Store<fromApp.AppState>) { }

  isLoading: boolean = true;
  user: User | null = null;
  storeSubs?: Subscription;

  ngOnInit(): void {
    this.storeSubs = this.store.select('usersReducer')
      .subscribe(usersState => {
        this.isLoading = usersState.loading;
        this.user = usersState.selectedUser;
      });
  }

  ngOnDestroy(): void {
    this.storeSubs?.unsubscribe();
  }

}
