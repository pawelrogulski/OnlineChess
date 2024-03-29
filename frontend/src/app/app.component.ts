import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { ChessboardComponent } from "./chessboard/chessboard.component";
import { HttpClientModule } from '@angular/common/http';
import { DataService } from './service/data.service';

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
  constructor(private dataService: DataService, private router: Router) {}

  ngOnInit() {
    const playerId = this.dataService.getPlayerId();
    if (playerId.length>10) {
      this.router.navigate(['/gameMode']);
    } else {
      this.router.navigate(['/signUp']);
    }
  }
}
