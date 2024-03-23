import { Injectable } from '@angular/core';
import { Player } from '../player';
import { Observable } from 'rxjs';
import { HttpHeaders, HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient) {}

  getPlayerId(): string{
    let playerId =  sessionStorage.getItem('playerId');
    if(playerId == null){
      playerId = "";
    }
    return playerId;
  }

  setPlayerId(playerId: string): void {
    sessionStorage.setItem('playerId', playerId);
  }
  checkSession(): void {
    const playerId = this.getPlayerId();
    const headers = new HttpHeaders().set('Authorization', playerId);
    this.http.post<Player>('http://localhost:8080/api/auth/signIn', null, { headers }).subscribe(data =>{
      this.setPlayerId(data.playerId);
    });
  }
  signUp(username : string): void {
    this.http.post<string>('http://localhost:8080/api/auth/signUp', {username}).subscribe(data =>{
      this.setPlayerId(data);
    });
  }
}
