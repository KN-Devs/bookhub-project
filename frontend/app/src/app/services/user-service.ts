import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../Interface/user';

@Injectable({
  providedIn: 'root',
})
export class UserService {


  constructor(private http : HttpClient) {
  }

  getUserInfo() {
    return this.http.get<User>('http://localhost:8080/api/users/info');
  }

}
