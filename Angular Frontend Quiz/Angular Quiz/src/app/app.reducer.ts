import { ActionReducerMap } from '@ngrx/store';
import * as fromUsers from './/store/users.reducer';

export interface AppState {
    usersReducer : fromUsers.UsersState;
};

export const appReducer : ActionReducerMap<AppState> = {
    usersReducer : fromUsers.usersReducer,
}