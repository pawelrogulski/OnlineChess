import { GameModeService } from './../service/game-mode.service';
import { CommonModule } from '@angular/common';
import { DisplayService } from '../service/display.service';
import { MoveService } from '../service/move.service';
import { Piece } from './../piece';
import { Component, OnInit } from '@angular/core';
import { Move } from '../move';
import { NotificationService } from '../service/notification.service';
import { DataService } from '../service/data.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-chessboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chessboard.component.html',
  styleUrl: './chessboard.component.css'
})
export class ChessboardComponent implements OnInit {
  pieces: Piece[] = [];
  moves: Move[] = [];
  currentPiece: number[] = []; //column and row
  notification: string = '';
  chessboardButtons: { id: string; text: string; textColor: string }[][] = [];
  whiteSide: boolean = true;//true if player is playing white pieces
  sideColor: number[] = this.whiteSide == true ? [7,6,5,4,3,2,1,0] : [0,1,2,3,4,5,6,7];
  constructor(private displayService: DisplayService, private moveService: MoveService, private notificationService: NotificationService, private router: Router) {}
  ngOnInit(): void {
    this.loadChessPieces();
    this.notificationService.getNotifications().subscribe(notification => {
      this.notification = notification;
      console.log(this.notification);
    });
  }

  loadChessPieces(): void {
      this.displayService.getGameData().subscribe(data => {
      this.pieces = data;
      this.generateButtonText();
    });
  }
  generateButtonText(): void {//piece or move
    for(let i = 0; i < this.pieces.length; i++) {
      const buttonId = `button_${this.pieces[i].col}_${this.pieces[i].row}`;
      const buttonElement = document.getElementById(buttonId) as HTMLButtonElement;
      buttonElement.style.color = this.pieces[i].color;
      buttonElement.innerHTML = this.getPieceCode(this.pieces[i].type);
    }
  }
  changeButtonBackgroundMove(cells: Move[]): void {
    cells.forEach(cell => {
      const buttonId = `button_${cell.col}_${cell.row}`;
      const buttonElement = document.getElementById(buttonId) as HTMLButtonElement;
      buttonElement.style.backgroundColor = 'lightblue';
    });
  }
  cleanBoard(cleanPieces: boolean): void {
    for(let i = 0; i <= 7; i++){
      for(let j = 0; j <= 7; j++){
        const buttonId = `button_${i}_${j}`;
        const buttonElement = document.getElementById(buttonId) as HTMLButtonElement;
        buttonElement.style.backgroundColor = (i+j)%2 == 0? "#095c02" : "#c9c2ae";
        if(cleanPieces){
          buttonElement.innerHTML = "";
        }
      }
    }
    if(cleanPieces){
      this.moves = [];
    }
  }
  getPieceCode(type: string): string {
    switch (type) {
      case 'BISHOP':
        return '&#9821;';
      case 'KING':
        return '&#9818;';
      case 'KNIGHT':
        return '&#9822;';
      case 'PAWN':
        return '&#9823;';
      case 'QUEEN':
        return '&#9819;';
      case 'ROOK':
        return '&#9820;';
      default:
        return '';
    }
  }
  handleButtonClick(col: number, row: number): void {
    this.cleanBoard(false);
    const buttonId = `button_${row}_${col}`;
    const buttonElement = document.getElementById(buttonId) as HTMLButtonElement;
    if (buttonElement) {
      let flagMove: boolean = false;
      this.moves.forEach(move => {
        if(move.col==col && move.row==row){
          flagMove=true;
        }
      })
      if(flagMove) {
        this.movePiece(col,row);
      }
      else{
        this.moveService.checkMoves(col, row).subscribe(data => {
          this.moves = data;
          this.changeButtonBackgroundMove(data);
          this.currentPiece = [col,row];
        }, error => {console.log(error)});
      }
    }
  }
  movePiece(col: number, row: number): void {
    this.moveService.movePiece(this.currentPiece[0], this.currentPiece[1] ,col, row).subscribe(data =>{
      this.pieces = data;
      this.cleanBoard(true);
      this.generateButtonText();
    })
  }
  newGame() {
    this.router.navigate(['/gameMode']);
  }

}
