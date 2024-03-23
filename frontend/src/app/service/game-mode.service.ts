import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DataService } from './data.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GameModeService {

  constructor(private http: HttpClient, private dataService: DataService) {}

  newSingleplayerGame():Observable<boolean>{
    const playerId = this.dataService.getPlayerId();
    const headers = new HttpHeaders().set('Authorization', playerId);
    return this.http.post<boolean>('http://localhost:8080/api/game/newSingleGame', null, { headers });
  }
  newtMultiplayerGame(){
    const playerId = this.dataService.getPlayerId();
    this.http.post('http://localhost:8080/api/game/newMultiGame', {playerId}).subscribe();
  }

}
