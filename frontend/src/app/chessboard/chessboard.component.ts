import { CommonModule } from '@angular/common';
import { DisplayService } from '../service/display.service';
import { Piece } from './../piece';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-chessboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chessboard.component.html',
  styleUrl: './chessboard.component.css'
})
export class ChessboardComponent implements OnInit {
  pieces: Piece[] = [];
  chessboardButtons: { id: string; text: string; textColor: string }[][] = [];
  whiteSide: boolean = true;//true if player is playing white pieces
  sideColor: number[] = this.whiteSide == true ? [7,6,5,4,3,2,1,0] : [0,1,2,3,4,5,6,7];
  constructor(private displayService: DisplayService) {}
  ngOnInit(): void {
    this.loadChessPieces();
  }

  loadChessPieces(): void {
    this.displayService.getGameData().subscribe(data => {
      this.pieces = data;
      console.log(this.pieces);
      this.generateButtonText();
    });
  }
  generateButtonText(): void {
    for(let i = 0; i < this.pieces.length; i++) {
      console.log("xd");
      const buttonId = `button_${this.pieces[i].col}_${this.pieces[i].row}`;
      const buttonElement = document.getElementById(buttonId) as HTMLButtonElement;
      buttonElement.style.color = this.pieces[i].color;
      buttonElement.innerHTML = this.getPieceCode(this.pieces[i].type);
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
  handleButtonClick(row: number, column: number): void {
    const buttonId = `button_${row}_${column}`;
    const buttonElement = document.getElementById(buttonId);
    if (buttonElement) {
      console.log('Clicked button with id:', buttonId);
    }
  }
}
