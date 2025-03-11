import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SingleUserURL, UsersURL } from '../../enviroments/enviroments';
import { User, UsersData } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  constructor(private http: HttpClient) { }

  getUsers(PageIndex: number): Observable<UsersData> {
    return this.http.get<UsersData>(UsersURL + PageIndex);
  }

  getSingleUser(Id: number): Observable<{ data: User }> {
    return this.http.get<{ data: User }>(SingleUserURL + Id);
  }

}

