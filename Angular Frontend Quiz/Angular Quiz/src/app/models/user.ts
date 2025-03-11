
export interface User {
    avatar: string,
    first_name: string,
    last_name: string,
    id: number
}

export type UsersData = {
    page: number,
    per_page: number,
    total: number,
    total_pages: number,
    data: User[];
};