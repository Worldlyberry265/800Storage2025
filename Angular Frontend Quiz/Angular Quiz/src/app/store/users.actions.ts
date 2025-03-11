import { createAction, props } from "@ngrx/store";
import { User } from "../models/user";

export const UsersFetchStart = createAction(
    '[Users] Fetch Start',
    props<{ pageIndex: number }>()
)

export const UsersFetchSuccess = createAction(
    '[Users] Fetch Success',
    props<{ Payload: { users: User[], total: number, pageIndex: number } }>()
)

export const UserFetchByIdStart = createAction(
    '[UserById] Fetch Start',
    props<{ userId: number, searchingForUser: boolean }>()
)

export const UserFetchByIdSuccess = createAction(
    '[UserById] Fetch Success',
    props<{ Payload: User }>()
)

export const UserFetchByIdFailure = createAction(
    '[UserById] Fetch Failure'
)

export const DeleteSelectedUser = createAction(
    '[User] Delete SelectedUser'
)
