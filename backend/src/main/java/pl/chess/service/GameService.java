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
        checkInputValues(col,row);
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
    public List<Move> calculateAvailableMoves(int col, int row){
        checkInputValues(col, row);
        if(getPieceAt(col,row).isEmpty())
            throw new IllegalArgumentException("No piece found at given cords");
        Piece piece = getPieceAt(col, row).get();
        return switch (piece.getType()){
                    case BISHOP -> calculateAvailableBishopMoves(piece);
                    case KING -> calculateAvailableKingMoves(piece);
                    case KNIGHT -> calculateAvailableKnightMoves(piece);
                    case PAWN -> calculateAvailablePawnMoves(piece);
                    case QUEEN -> calculateAvailableQueenMoves(piece);
                    case ROOK -> calculateAvailableRookMoves(piece);
                    default -> {throw new IllegalArgumentException("Chess type not found");}
        };
    }
    public List<Move> calculateAvailableBishopMoves(Piece piece){
        List<Move> moves = new ArrayList<>();
        /** bishop "x" moves can be calculated by adding same number (range 1 to 7) to col and row in 4 cases: both positive, 
         * positive and negative, negative and positive, both negative
         **/
        for(int j=-1;j<=1;j=j+2){
            for(int k=-1;k<=1;k=k+2){
                try{
                    for(int i=1;i<=7;i++){//2 loops to move in 2 dimensions, 3rd loop to move more than 1 cell
                        if(getPieceAt(piece.getCol()+i*j,piece.getRow()+i*k).isEmpty()){ //2nd part can be null and throw exception
                            moves.add(new Move(piece.getCol()+i*j,piece.getRow()+i*k, NORMAL));//empty cell
                        }
                        else if(getPieceAt(piece.getCol()+i*j,piece.getRow()+i*k).get().getColor()!=piece.getColor()){
                            moves.add(new Move(piece.getCol()+i*j,piece.getRow()+i*k, NORMAL));//opponent piece
                            break;
                        }
                        else{
                            break;//ally piece
                        }
                    }
                }catch (Exception e){}
            }
        }
        return calculateCastleMoves(moves, piece);
    }
    public List<Move> calculateAvailableKingMoves(Piece piece){
        List<Move> moves = new ArrayList<>();
        for(int i=-1+piece.getCol();i<=1;i++){
            for(int j=-1+piece.getRow();j<=1;j++){
                try{
                    if(getPieceAt(i,j).isEmpty() || getPieceAt(i,j).get().getColor()!=piece.getColor()){ //2nd part can be null and throw exception
                        moves.add(new Move(i,j, NORMAL));
                    }
                }catch (Exception e){}
            }
        }
        return calculateCastleMoves(moves, piece);
    }
    public List<Move> calculateCastleMoves(List<Move> moves, Piece piece){
        int side = piece.getColor()== WHITE ? 0 : 7;
        if(!board.whiteKingMoved){
            if(getPieceAt(5,side).isEmpty() && getPieceAt(6,side).isEmpty() && !board.whiteRightRookMoved){
                moves.add(new Move(6,side,CASTLE));
            }
            if(getPieceAt(1,side).isEmpty() && getPieceAt(2,side).isEmpty() && getPieceAt(3,side).isEmpty() && !board.whiteLeftRookMoved){
                moves.add(new Move(2,side,CASTLE));
            }
        }
        return moves;
    }
    public List<Move> calculateAvailableKnightMoves(Piece piece){
        List<Move> moves = new ArrayList<>();
        for(int i=-2+piece.getCol();i<=2;i++){
            for(int j=-2+piece.getRow();j<=2;j++){
                if(i==0 || j==0 || (Math.abs(i)==2 && Math.abs(j)==2))
                    break;//not "L" move
                try{
                    if(getPieceAt(i,j).isEmpty() || getPieceAt(i,j).get().getColor()!=piece.getColor()){ //2nd part can be null and throw exception
                        moves.add(new Move(i,j, NORMAL));
                    }
                }catch (Exception e){}
            }
        }
        return moves;
    }
    public List<Move> calculateAvailablePawnMoves(Piece piece){
        List<Move> moves = new ArrayList<>();
        int side = piece.getColor()== WHITE ? 1 : -1;
        if(getPieceAt(piece.getCol(),piece.getRow()+side).isEmpty()){
            moves.add(new Move(piece.getCol(), piece.getRow()+side, NORMAL));//move 1 cell forward
            calculateAvailableDoubleJump(moves, piece);
        }
        for(int i=-1;i<=1;i=i+2){
            if(getPieceAt(piece.getCol()+i,piece.getRow()+side).isPresent() && getPieceAt(piece.getCol()+i,piece.getRow()+side).get().getColor()!=piece.getColor()){
                moves.add(new Move(piece.getCol()+i,piece.getRow()+side,NORMAL));//move and capture
            }
        }
        return calculateAvailableEnPassant(moves, piece);
    }
    public List<Move> calculateAvailableDoubleJump(List<Move> moves, Piece piece){
        int side = piece.getColor()== WHITE ? 5 : 0;
        if(piece.getRow()+side==6){
            moves.add(new Move(piece.getCol(), piece.getRow()-2+4*(side/5), DOUBLE_JUMP));//if white add 2, if black substract 2
            //en passant
            board.enPassantCol=piece.getCol();
            board.enPassantRow=piece.getRow()-1+2*(side/5);
        }
        return moves;
    }
    public List<Move> calculateAvailableEnPassant(List<Move> moves, Piece piece){
        if(board.enPassantCol==-1) 
            return moves;//not available
        int side = piece.getColor()== WHITE ? 5 : 2;
        if(piece.getRow()==side && Math.abs(piece.getCol()-board.enPassantCol)==1){// "v" or "^" move
            moves.add(new Move(board.enPassantCol, board.enPassantRow, EN_PASSANT));
        }
        return moves;
    }
    public List<Move> calculateAvailableQueenMoves(Piece piece){
        //Queen moves are sum of bishop and rook moves
        List<Move> moves = calculateAvailableBishopMoves(piece);
        moves.addAll(calculateAvailableRookMoves(piece));
        return moves;
    }
    public List<Move> calculateAvailableRookMoves(Piece piece){
        List<Move> moves = new ArrayList<>();
        /** rook "+" moves can be calculated by adding values 1 to 7 to col, 1 to 7 to row, -1 to -7 to col, -1 to -7 to row
         * first 2 loops are for moving in 2 dimensions, then moves in 1 line are added till meeting piece (break) or out of board (catch)
         */
        for(int k=-1;k<=1;k=k+2){//1- up and right, -1 - down and left
            for(int j=0;j<=1;j++){//"switch" - if 0 - cols, if 1 - rows
                try{
                    for(int i=1;i<=7;i++){//2 loops to move in 2 dimensions, 3rd loop to move more than 1 cell
                        if(getPieceAt(piece.getCol()+i*(k*(j-1)),piece.getRow()+i*k*j).isEmpty()){ //2nd part can be null and throw exception
                            moves.add(new Move(piece.getCol()+i*(k*(j-1)),piece.getRow()+i*k*j, NORMAL));//empty cell
                        }
                        else if(getPieceAt(piece.getCol()+i*(k*(j-1)),piece.getRow()+i*k*j).get().getColor()!=piece.getColor()){
                            moves.add(new Move(piece.getCol()+i*(k*(j-1)),piece.getRow()+i*k*j, NORMAL));//opponent piece
                            break;
                        }
                        else{
                            break;//ally piece
                        }
                    }
                }catch (Exception e){}
            }           
        }
        return moves;
    }
}
