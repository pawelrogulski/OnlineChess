import { SignUpComponent } from './sign-up/sign-up.component';
import { Routes, RouterModule } from '@angular/router';
import { ChessboardComponent } from './chessboard/chessboard.component';
import { AppComponent } from './app.component';
import { GameModeComponent } from './game-mode/game-mode.component';

export const routes: Routes = [
  { path: '', component: AppComponent },
  { path: 'signUp', component: SignUpComponent },
  { path: 'gameMode', component: GameModeComponent},
  { path: 'game', component: ChessboardComponent }
];

