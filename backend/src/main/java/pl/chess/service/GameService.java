package pl.chess.service;

import pl.chess.domain.*;

import static pl.chess.domain.Piece.Color.*;


public class GameService {
    public void initializeBoard(Board board){
        Piece[][] pieces = new Piece[board.height][board.width];
        pieces[0][0] = new Rook(BLACK);
        pieces[0][1] = new Knight(BLACK);
        pieces[0][2] = new Bishop(BLACK);
        pieces[0][3] = new Queen(BLACK);
        pieces[0][4] = new King(BLACK);
        pieces[0][5] = new Bishop(BLACK);
        pieces[0][6] = new Knight(BLACK);
        pieces[0][7] = new Rook(BLACK);
        for(int i=0;i<board.width;i++){
            pieces[1][i] = new Pawn(BLACK);
        }
        for(int i=0;i<board.width;i++){
            pieces[6][i] = new Pawn(WHITE);
        }
        pieces[7][0] = new Rook(BLACK);
        pieces[7][1] = new Knight(BLACK);
        pieces[7][2] = new Bishop(BLACK);
        pieces[7][3] = new King(BLACK);
        pieces[7][4] = new Queen(BLACK);
        pieces[7][5] = new Bishop(BLACK);
        pieces[7][6] = new Knight(BLACK);
        pieces[7][7] = new Rook(BLACK);
    }
}
