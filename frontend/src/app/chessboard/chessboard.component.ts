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

  constructor(private displayService: DisplayService) {}
  ngOnInit(): void {
    this.loadChessPieces();
  }

  loadChessPieces(): void {
    this.displayService.getGameData().subscribe(data => {
      this.pieces = data;
    });
  }
}
