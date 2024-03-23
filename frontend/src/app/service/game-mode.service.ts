import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root'
})
export class GameModeService {

  constructor(private http: HttpClient, private dataService: DataService) {}

  newtSingleplayerGame(){
    const playerId = this.dataService.getPlayerId();
    const headers = new HttpHeaders().set('Authorization', playerId);
    this.http.post('http://localhost:8080/api/game/newSingleGame', null, { headers }).subscribe(
      () => {
        console.log('Żądanie POST zostało wysłane pomyślnie');
      },
      (error) => {
        console.error('Wystąpił błąd podczas wysyłania żądania POST:', error);
      }
    );
  }
  newtMultiplayerGame(){
    const playerId = this.dataService.getPlayerId();
    this.http.post('http://localhost:8080/api/game/newMultiGame', {playerId}).subscribe();
  }

}
