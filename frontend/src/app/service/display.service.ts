import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Piece } from '../piece';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root'
})
export class DisplayService {

  constructor(private http: HttpClient, private dataService: DataService) {}

  getGameData(): Observable<Piece[]> {
    const headers = new HttpHeaders().set('Authorization', this.dataService.getPlayerId());
    return this.http.get<Piece[]>('http://localhost:8080/api/game/display', { headers });
  }
  getUsername(): Observable<string>{
    const headers = new HttpHeaders().set('Authorization', this.dataService.getPlayerId());
    return this.http.get('http://localhost:8080/api/auth/getUsername', { headers, responseType: 'text' });
  }
  getEnemyUsername(): Observable<string>{
    const headers = new HttpHeaders().set('Authorization', this.dataService.getPlayerId());
    return this.http.get('http://localhost:8080/api/auth/getEnemyUsername', { headers: headers, responseType: 'text' });
  }
}
