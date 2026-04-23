import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Categories} from '../Interface/categories';

@Injectable({
  providedIn: 'root',
})
export class CategoriesService {

  constructor(private http : HttpClient) {
  }

  getAllCategories() {
    return this.http.get<Categories[]>('http://localhost:8080/api/categories');
  }

}
