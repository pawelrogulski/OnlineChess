import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Move } from '../move';
import { Piece } from '../piece';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root'
})
export class MoveService {

  constructor(private http: HttpClient, private dataService: DataService) {}

  checkMoves(col: number, row: number): Observable<Move[]> {
    const headers = new HttpHeaders().set('Authorization', this.dataService.getPlayerId());
    const url = 'http://localhost:8080/api/game/checkMoves';
    let params = new HttpParams()
      .set('col', col)
      .set('row', row);
    return this.http.get<Move[]>(url, {params, headers });
  }

  movePiece(colOrigin: number, rowOrigin: number, colTarget: number, rowTarget: number): Observable<Piece[]>{
    const headers = new HttpHeaders().set('Authorization', this.dataService.getPlayerId());
    const url = 'http://localhost:8080/api/game/move';
    const body = {
      colOrigin: colOrigin,
      rowOrigin: rowOrigin,
      colTarget: colTarget,
      rowTarget: rowTarget
    };
    return this.http.post<Piece[]>(url, body, {headers});
  }
}
