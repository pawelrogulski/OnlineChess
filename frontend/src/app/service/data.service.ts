import { Injectable } from '@angular/core';
import { Player } from '../player';
import { Observable } from 'rxjs';
import { HttpHeaders, HttpClient, HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient, private router: Router) {}

  getPlayerId(): string{
    let playerId =  sessionStorage.getItem('playerId');
    if(playerId == null){
      playerId = "";
    }
    if(playerId=="undefined"){
      this.router.navigate(['/signUp']);
    }
    return playerId;
  }

  setPlayerId(playerId: string): void {
    if(playerId != "undefined"){
      window.sessionStorage.removeItem('playerId');
      window.sessionStorage.setItem('playerId',playerId);
    }
  }
  checkSession(): boolean {
    const playerId = this.getPlayerId();
    const headers = new HttpHeaders().set('Authorization', playerId);
    this.http.post<Player>('http://localhost:8080/api/auth/signIn', null, { headers, observe: 'response' }).subscribe(
      (response: HttpResponse<Player>) => {
        if(response.status !== 200 || response.body==null){
          return false;
        }
        this.setPlayerId(response.body.playerId);
        return true;
      }
    );
    return false;
  }
  signUp(username : string): void {
    this.http.post<string>('http://localhost:8080/api/auth/signUp', username).subscribe(data =>{
      this.setPlayerId(data);
    });
  }
}
