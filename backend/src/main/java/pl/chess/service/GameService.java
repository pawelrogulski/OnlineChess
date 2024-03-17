package pl.chess.service;

import org.springframework.stereotype.Service;
import pl.chess.domain.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pl.chess.domain.Piece.Color.*;
import static pl.chess.domain.Piece.Type.*;
import static pl.chess.domain.Move.Type.*;

@Service
public class GameService {
    Board board = initializeBoard();
    public List<Piece> getBoard(){
        return board.pieces;
    }
    public Board initializeBoard(){
        Board initializedBoard = new Board();
        //set pieces position at the start of game
        Piece.Type[] types = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
        for(int i=0;i<8;i++){
            initializedBoard.pieces.add(new Piece(i,0,WHITE, types[i]));
        }
        for(int i=0;i<8;i++){
            initializedBoard.pieces.add(new Piece(i,1,WHITE,PAWN));
        }
        for(int i=0;i<8;i++){
            initializedBoard.pieces.add(new Piece(i,6,BLACK,PAWN));
        }
        for(int i=0;i<8;i++){
            initializedBoard.pieces.add(new Piece(i,7,BLACK, types[i]));
        }
        return initializedBoard;
    }
    public Optional<Piece> getPieceAt(int col, int row){
        return board.pieces.stream()
                .filter(piece -> piece.getCol() == col && piece.getRow() == row)
                .findFirst();
    }
    public void checkInputValues(int col, int row){
        if(col<0 || row<0){
            throw new IllegalArgumentException("Cords can't be negative");
        }
        if(col>7 || row>7){
            throw new IllegalArgumentException("Cords can't be larger that 7");
        }
    }
    public void checkInputValues(int colOrigin, int rowOrigin, int colTarget, int rowTarget){
        if(colOrigin<0 || rowOrigin<0 || colTarget<0 || rowTarget<0){
            throw new IllegalArgumentException("Cords can't be negative");
        }
        if(colOrigin>7 || rowOrigin>7 || colTarget>7 || rowTarget>7){
            throw new IllegalArgumentException("Cords can't be larger that 7");
        }
        if(colOrigin==colTarget &&  rowOrigin==rowTarget){
            throw new IllegalArgumentException("Origin and target must be different");
        }
    }
    public void movePiece(int colOrigin, int rowOrigin, int colTarget, int rowTarget){
        //move, capture, castle, en passant
        checkInputValues(colOrigin, rowOrigin, colTarget, rowTarget);
        getPieceAt(colOrigin,rowOrigin).ifPresentOrElse(
                originPiece -> {
                    removePiece(colTarget,rowTarget);
                    originPiece.setCol(colTarget);
                    originPiece.setRow(rowTarget);
                    },
                () -> {throw new IllegalArgumentException("No piece found at given cords");}
        );
    }
    public void removePiece(int col, int row){
        getPieceAt(col,row).ifPresent(
                capturedPiece -> board.pieces.remove(capturedPiece)//if piece exist on chosen cords remove it
        );
    }
    public void calculateAvailableMoves(int col, int row){
        checkInputValues(col, row);
        getPieceAt(col,row).ifPresentOrElse(
                piece -> switch (piece.getType()){
                    case BISHOP -> null;
                    case KING -> calculateAvailableKingMoves(piece);
                    case KNIGHT -> null;
                    case PAWN -> null;
                    case QUEEN -> null;
                    case ROOK -> null;
                }
        );
    }
    public void calculateAvailableBishopMoves(Piece piece, int col, int row){

    }
    public void calculateAvailableKingMoves(Piece piece, int col, int row){
        List<Move> moves = new ArrayList<>();
        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                try{
                    if(getPieceAt(i,j).get().getCol()==piece.getCol() || getPieceAt(i,j).isEmpty()){
                        moves.add(new Move(i,j, NORMAL));
                    }
                }catch (Exception e){}
            }
        }
    }
    public void calculateCastleMoves(List<Move> moves, Piece piece){
        if(piece.getColor()==WHITE){
            if(!board.whiteKingMoved){
                if(getPieceAt(5,0).isEmpty() && getPieceAt(6,0).isEmpty() && !board.whiteRightRookMoved){
                    moves.add(new Move(6,0,CASTLE));
                }
                if(getPieceAt(1,0).isEmpty() && getPieceAt(2,0).isEmpty() && getPieceAt(3,0).isEmpty() && !board.whiteLeftRookMoved){
                    moves.add(new Move(2,0,CASTLE));
                }
            }
        }
        else {
            if(!board.blackKingMoved){
                if(getPieceAt(5,7).isEmpty() && getPieceAt(6,7).isEmpty() && !board.blackRightRookMoved){
                    moves.add(new Move(6,7,CASTLE));
                }
                if(getPieceAt(1,0).isEmpty() && getPieceAt(2,7).isEmpty() && getPieceAt(3,0).isEmpty() && !board.blackLeftRookMoved){
                    moves.add(new Move(2,7,CASTLE));
                }
            }
        }
    }
    public void calculateAvailableKnightMoves(Piece piece, int col, int row){

    }
    public void calculateAvailablePawnMoves(Piece piece, int col, int row){

    }
    public void calculateAvailableQueenMoves(Piece piece, int col, int row){

    }
    public void calculateAvailableRookMoves(Piece piece, int col, int row){

    }
}
