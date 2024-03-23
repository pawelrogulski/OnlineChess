import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { ChessboardComponent } from "./chessboard/chessboard.component";
import { HttpClientModule } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { DataService } from './service/data.service';
import { NotificationService } from './service/notification.service';

@Component({
    selector: 'app-root',
    standalone: true,
    templateUrl: './app.component.html',
    styleUrl: './app.component.css',
    imports: [RouterOutlet, ChessboardComponent, HttpClientModule]
})
export class AppComponent implements OnInit {
  username: string = '';
  multiplayer: boolean = false;
  constructor(private dataService: DataService, private router: Router, private http: HttpClient) {}

  ngOnInit() {
    const playerId = this.dataService.getPlayerId();
    if (playerId!=null && playerId!=="undefined") {
      this.dataService.checkSession();
      this.router.navigate(['/gameMode']);
    } else {
      this.router.navigate(['/signUp']);
    }
  }
  // startGame() {
  //   this.http.post<any>('/api/start-game', { username: this.username, gameMode: this.multiplayer ? 'multiplayer' : 'singleplayer' })
  //     .subscribe(response => {
  //       const userId = response.userId;
  //       document.cookie = `userId=${userId}`;
  //       window.location.href = '/choose-game-mode';
  //     }, error => {
  //       console.error('Error starting game:', error);
  //     });
  // }
}
