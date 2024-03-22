import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { ChessboardComponent } from "./chessboard/chessboard.component";
import { HttpClientModule } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';

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
  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit() {
    // forward to /display
    //this.router.navigate(['/display']);
  }
  startGame() {
    this.http.post<any>('/api/start-game', { username: this.username, gameMode: this.multiplayer ? 'multiplayer' : 'singleplayer' })
      .subscribe(response => {
        const userId = response.userId;
        document.cookie = `userId=${userId}`;
        window.location.href = '/choose-game-mode';
      }, error => {
        console.error('Error starting game:', error);
      });
  }
}
