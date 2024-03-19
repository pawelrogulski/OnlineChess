import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Move } from '../move';
import { Piece } from '../piece';

@Injectable({
  providedIn: 'root'
})
export class MoveService {

  constructor(private http: HttpClient) {}

  checkMoves(col: number, row: number): Observable<Move[]> {
    const url = 'http://localhost:8080/api/game/checkMoves';
    let params = new HttpParams()
      .set('col', col)
      .set('row', row);
    return this.http.get<Move[]>(url, {params: params });
  }

  movePiece(colOrigin: number, rowOrigin: number, colTarget: number, rowTarget: number): Observable<Piece[]>{
    const url = 'http://localhost:8080/api/game/move';
    const body = {
      colOrigin: colOrigin,
      rowOrigin: rowOrigin,
      colTarget: colTarget,
      rowTarget: rowTarget
    };
    return this.http.post<Piece[]>(url, body);
  }
}
