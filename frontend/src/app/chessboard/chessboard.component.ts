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
  buttonText: string[] = [];

  constructor(private displayService: DisplayService) {}
  ngOnInit(): void {
    this.loadChessPieces();
    this.generateButtonText();
  }

  loadChessPieces(): void {
    this.displayService.getGameData().subscribe(data => {
      this.pieces = data;
    });
  }
  generateButtonText(): void {
    const letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
    const numbers = ['1', '2', '3', '4', '5', '6', '7', '8'];

    for (let i = 0; i < 8; i++) {
      for (let j = 0; j < 8; j++) {
        if (i === 0) {
          this.buttonText.push(letters[j]);
        } else if (j === 0) {
          this.buttonText.push(numbers[i - 1]);
        } else {
          this.buttonText.push('');
        }
      }
    }
  }
}
