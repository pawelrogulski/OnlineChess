import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { GameModeService } from '../service/game-mode.service';

@Component({
  selector: 'app-game-mode',
  standalone: true,
  imports: [],
  templateUrl: './game-mode.component.html',
  styleUrl: './game-mode.component.css'
})
export class GameModeComponent {
  constructor(private router: Router, private gameModeService: GameModeService) { }
  selectSingleplayer(){
    this.gameModeService.newtSingleplayerGame();
    this.router.navigate(['/game']);
  }
  selectMultiplayer(){
    this.gameModeService.newtMultiplayerGame();
    this.router.navigate(['/game']);
  }
}
