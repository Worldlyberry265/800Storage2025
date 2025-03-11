import { Actions, createEffect, ofType } from "@ngrx/effects";
import { catchError, delay, map, of, switchMap, take } from "rxjs";
import { ClientService } from "../Services/http.client";
import * as UsersActions from '../store/users.actions';
import { Injectable } from "@angular/core";
import { User, UsersData } from "../models/user";
import { Store } from "@ngrx/store";
import * as fromApp from '../app.reducer';

@Injectable()
export class UsersEffects {

    constructor(private $actions: Actions, private httpClient: ClientService, private store: Store<fromApp.AppState>) { }


    usersFetch = createEffect(() => this.$actions.pipe(
        ofType(UsersActions.UsersFetchStart),
        switchMap(({ pageIndex }) => {
            return this.httpClient.getUsers(pageIndex)
        }),
        delay(1000), // just to simulate the waiting time for an actual backend response to be returned
        map(data => {
            return UsersActions.UsersFetchSuccess({ Payload: { pageIndex: data.page, total: data.total, users: data.data } });
        })
    ));

    userFetchById = createEffect(() => this.$actions.pipe(
        ofType(UsersActions.UserFetchByIdStart),
        switchMap(({ userId }) => {
            return this.store.select('usersReducer').pipe(
                // We take only 1 to check if the user is found in the store or not
                take(1),
                map(state => {
                    return state.users.find(user => user.id === userId);
                }),
                switchMap((user?: User) => {
                    // if the user is stored, we return it, if not we send a new http req to fetch it.
                    if (user) {
                        return of(UsersActions.UserFetchByIdSuccess({ Payload: user }));
                    } else {
                        return this.httpClient.getSingleUser(userId).pipe(
                            map(fetchedUser => UsersActions.UserFetchByIdSuccess({ Payload: fetchedUser.data })),
                            // we return an of if there is an error to keep the flow of the effect.
                            catchError(() => of(UsersActions.UserFetchByIdFailure()))
                        );
                    }
                })
            );
        })
    ));

}