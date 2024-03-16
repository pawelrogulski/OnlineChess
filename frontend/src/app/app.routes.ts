import { Routes, RouterModule } from '@angular/router';
import { ChessboardComponent } from './chessboard/chessboard.component';

export const routes: Routes = [
  { path: 'display', component: ChessboardComponent }
];

export class AppRoutingModule { }
