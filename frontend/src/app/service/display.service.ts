import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Piece } from '../piece';

@Injectable({
  providedIn: 'root'
})
export class DisplayService {

  constructor(private http: HttpClient) {}

  getGameData(): Observable<Piece[]> {
    return this.http.get<Piece[]>('http://localhost:8080/api/game/display');
  }
}
