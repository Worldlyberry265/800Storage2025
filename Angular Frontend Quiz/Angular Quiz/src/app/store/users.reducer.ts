import { createReducer, on } from "@ngrx/store";
import { User } from "../models/user";
import * as UsersActions from "./users.actions";


export interface UsersState {
    usersTotal: number,
    users: User[];
    loading: boolean; // for displaying the loading bar
    selectedUser: User | null;
    searchingForUser: boolean; // to display only 1 user in the table when we search for one
    pageIndex: number;
}

const initialState: UsersState = {
    usersTotal: 0,
    users: [],
    loading: true,
    selectedUser: null,
    searchingForUser: false,
    pageIndex: 1
}

export const usersReducer = createReducer(
    initialState,

    on(UsersActions.UsersFetchStart, (state) => {
        return {
            ...state,
            searchingForUser: false,
            loading: true,
        };
    }),

    on(UsersActions.UsersFetchSuccess, (state, Payload) => {
        const data = Payload.Payload;
        return {
            ...state,
            usersTotal: data.total,
            users: data.users,
            loading: false,
            selectedUser: null,
            pageIndex: data.pageIndex
        };
    }),

    on(UsersActions.UserFetchByIdStart, (state, payload) => {
        return {
            ...state,
            searchingForUser: state.searchingForUser || payload.searchingForUser,
            loading: true,
            selectedUser: null,
        };
    }),

    on(UsersActions.UserFetchByIdSuccess, (state, Payload) => {
        let selectedUser = Payload.Payload;
        console.log("success");
        console.log(state);
        return {
            ...state,
            loading: false,
            selectedUser: selectedUser,
        };
    }),

    on(UsersActions.UserFetchByIdFailure, (state) => {
        console.log("start");
        console.log(state);
        return {
            ...state,
            loading: false,
            selectedUser: null,
        };
    }),

    on(UsersActions.DeleteSelectedUser, (state) => {
        return {
            ...state,
            selectedUser: null,
            searchingForUser: false
        };
    }),
);
