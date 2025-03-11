import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';


import { Store } from '@ngrx/store';
import * as UsersActions from '../store/users.actions';
import * as fromApp from '../app.reducer';

export const userResolver: ResolveFn<any> = (route: ActivatedRouteSnapshot) => {
  // we are sending false for searchingForUser because we aren't searching for a user, we are selecting a user, so when
  // we go back to the users list, the whole users list is displayed, not only this selected user.
  const store = inject(Store<fromApp.AppState>);
  store.dispatch(UsersActions.UserFetchByIdStart({ userId: +route.params['id'], searchingForUser: false }));
};

