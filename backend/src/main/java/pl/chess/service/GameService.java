package pl.chess.service;

import org.springframework.stereotype.Service;
import pl.chess.domain.*;


import static pl.chess.domain.Piece.Color.*;
import static pl.chess.domain.Piece.Type.*;

@Service
public class GameService {
    public void initializeBoard(Board board){
        Piece.Type[] types = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK}; //order on white side
        for(int i=0;i<board.width;i++){
            board.pieces.add(new Piece(i,0,WHITE, types[i]));
        }
        for(int i=0;i<board.width;i++){
            board.pieces.add(new Piece(i,1,WHITE,PAWN));
        }
        for(int i=0;i<board.width;i++){
            board.pieces.add(new Piece(i,6,BLACK,PAWN));
        }
        for(int i=0;i<board.width;i++){//reversed order of pieces to inverse king with queen on black side
            board.pieces.add(new Piece(i,7,BLACK, types[board.width-i-1]));
        }
    }
}
