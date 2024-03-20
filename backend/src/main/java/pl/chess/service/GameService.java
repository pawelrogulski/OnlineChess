package pl.chess.service;

import org.springframework.stereotype.Service;
import pl.chess.domain.*;


import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<Piece> getPieceAt(int col, int row, List<Piece> pieces){//method for checking pseudo legal moves
        checkInputValues(col,row);//if optional is empty then there is no piece in the cell, if cords out of board then exception
        return pieces.stream()
                .filter(piece -> piece.getCol() == col && piece.getRow() == row)
                .findFirst();
    }
    public Optional<Piece> getPieceAt(int col, int row){//default method for board
        return getPieceAt(col,row,board.pieces);
    }
    public void checkInputValues(int col, int row){
        if(col<0 || row<0){
            throw new IllegalArgumentException("Cords can't be negative. Col:"+col+" row:"+row);
        }
        if(col>7 || row>7){
            throw new IllegalArgumentException("Cords can't be larger that 7. Col:"+col+" row:"+row);
        }
    }
    public void checkInputValues(int colOrigin, int rowOrigin, int colTarget, int rowTarget){
        if(colOrigin<0 || rowOrigin<0 || colTarget<0 || rowTarget<0){
            throw new IllegalArgumentException("Cords can't be negative. ColOrigin:"+colOrigin+" rowOrigin:"+rowOrigin+" colTarget:"+colTarget+" rowTarget:"+rowTarget);
        }
        if(colOrigin>7 || rowOrigin>7 || colTarget>7 || rowTarget>7){
            throw new IllegalArgumentException("Cords can't be larger that 7. ColOrigin:"+colOrigin+" rowOrigin:"+rowOrigin+" colTarget:"+colTarget+" rowTarget:"+rowTarget);
        }
        if(colOrigin==colTarget &&  rowOrigin==rowTarget){
            throw new IllegalArgumentException("Origin and target must be different. ColOrigin:"+colOrigin+" rowOrigin:"+rowOrigin+" colTarget:"+colTarget+" rowTarget:"+rowTarget);
        }
    }
    public void movePiece(int colOrigin, int rowOrigin, int colTarget, int rowTarget){
        checkInputValues(colOrigin,rowOrigin,colTarget,rowTarget);
        Piece piece = getPieceAt(colOrigin, rowOrigin)
                .orElseThrow(() -> new IllegalArgumentException("Empty cell selected as piece to move"));
        Move target = calculateLegalMoves(colOrigin, rowOrigin)
                .stream()
                .filter(move -> move.getCol()==colTarget && move.getRow()==rowTarget)//is there a move that allows to go there?
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Piece can't move to selected cell"));
        board.enPassantCol=-1;
        board.enPassantRow=-1;//reset en passant
        movePiece(piece, target, board.pieces);
    }
    public void movePiece(Piece piece, Move move, List<Piece> pieces){
        //move, capture, castle, en passant
        switch (move.getType()){
            case NORMAL -> normalMove(piece,move,pieces);
            case DOUBLE_JUMP -> doubleJumpMove(piece,move,pieces);//set en passant
            case CASTLE -> castleMove(piece,move,pieces);
            case EN_PASSANT -> EnPassantMove(piece,move,pieces);
        }
    }
    public void removePiece(int col, int row, List<Piece> pieces){
        pieces
                .stream()
                .filter(piece -> piece.getCol()==col && piece.getRow()==row)
                .findFirst()
                .ifPresent(piece -> pieces.remove(piece));
    }
    public void normalMove(Piece piece, Move move, List<Piece> pieces){
        removePiece(move.getCol(), move.getRow(), pieces);
        piece.setCol(move.getCol());
        piece.setRow(move.getRow());
    }
    public void doubleJumpMove(Piece piece, Move move, List<Piece> pieces){
        removePiece(move.getCol(), move.getRow(), pieces);
        piece.setCol(move.getCol());
        piece.setRow(move.getRow());
        board.enPassantCol=piece.getCol();
        board.enPassantRow = piece.getColor()==WHITE ? 2 : 5;
    }
    public void castleMove(Piece king, Move move, List<Piece> pieces){
        int rookCol = move.getCol() == 6 ? 7 : 0;
        int rookRow = move.getRow();
        Piece rook = getPieceAt(rookCol, rookRow).orElseThrow(() -> new IllegalArgumentException ("Rook not found"));
        king.setCol(move.getCol());
        rook.setCol(3 + 2*rookCol/7);//if on column 0, then 3, if on column 7 then 5
        //rows stays same
    }
    public void EnPassantMove(Piece piece, Move move, List<Piece> pieces){
        Piece capturedPawn = getPieceAt(move.getCol(), piece.getRow()).orElseThrow(() -> new IllegalArgumentException("Pawn not found"));
        removePiece(capturedPawn.getCol(),capturedPawn.getRow(),pieces);
        piece.setCol(move.getCol());
        piece.setRow(move.getRow());
    }
    public List<Move> calculateLegalMoves(int col, int row){
        checkInputValues(col, row);
        if(getPieceAt(col,row).isEmpty())
            throw new IllegalArgumentException("No piece found at given cords");
        Piece piece = getPieceAt(col, row).get();
        List<Move> moves = calculateAvailableMoves(piece);
        deleteIllegalMoves(piece ,moves);
        return moves;
    }
    public List<Move> calculateAvailableMoves(Piece piece){
        return switch (piece.getType()){
                    case BISHOP -> calculateAvailableBishopMoves(piece);
                    case KING -> calculateAvailableKingMoves(piece);
                    case KNIGHT -> calculateAvailableKnightMoves(piece);
                    case PAWN -> calculateAvailablePawnMoves(piece);
                    case QUEEN -> calculateAvailableQueenMoves(piece);
                    case ROOK -> calculateAvailableRookMoves(piece);
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
        for(int i=-2;i<=2;i++){
            for(int j=-2;j<=2;j++){
                if(!(i==0 || j==0 || Math.abs(i)==Math.abs(j))) {//not "L" move
                    try{
                        if(getPieceAt(i+piece.getCol(),j+piece.getRow()).isEmpty() || getPieceAt(i+piece.getCol(),j+piece.getRow()).get().getColor()!=piece.getColor()){ //2nd part can be null and throw exception
                            moves.add(new Move(i+piece.getCol(),j+piece.getRow(), NORMAL));
                        }
                    }catch (Exception e){}
                }
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
            try{
                if(getPieceAt(piece.getCol()+i,piece.getRow()+side).isPresent() && getPieceAt(piece.getCol()+i,piece.getRow()+side).get().getColor()!=piece.getColor()){
                    moves.add(new Move(piece.getCol()+i,piece.getRow()+side,NORMAL));//move and capture
                }
            }catch (Exception e){}
        }
        return calculateAvailableEnPassant(moves, piece);
    }
    public List<Move> calculateAvailableDoubleJump(List<Move> moves, Piece piece){
        int side = piece.getColor()== WHITE ? 5 : 0;
        if(piece.getRow()+side==6){
            moves.add(new Move(piece.getCol(), piece.getRow()-2+4*(side/5), DOUBLE_JUMP));//if white add 2, if black substract 2
        }
        return moves;
    }
    public List<Move> calculateAvailableEnPassant(List<Move> moves, Piece piece){
        if(board.enPassantCol==-1) 
            return moves;//not available
        int side = piece.getColor()== WHITE ? 4 : 3;
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
    public List<Move> deleteIllegalMoves(Piece piece, List<Move> moves){
        for(Move pseudoLegalMove : moves){
            //clone board and pieces
            List<Piece> clonedBoard = new ArrayList<>(board.pieces.size());
            board.pieces
                    .stream()
                    .forEach(originPiece -> clonedBoard.add(new Piece(originPiece)));
            //get reference to cloned piece on origin piece cords
            Piece clonedPiece = getPieceAt(piece.getCol(), piece.getRow(), clonedBoard).orElseThrow(IllegalArgumentException::new);
            //make pseudo legal move
            movePiece(clonedPiece,pseudoLegalMove,clonedBoard);
            //if king is checked after move then move was illegal
            if(isCheck(clonedPiece.getColor(), clonedBoard)){
                moves.remove(pseudoLegalMove);
            }
        }
        return moves;
    }
    public boolean isCheck(Piece.Color kingColor, List<Piece> pieces){
        Piece king = getKing(kingColor);
        List<Move> moves = new ArrayList<>();
        for (Piece piece : pieces){
            if(piece.getColor()!=kingColor){
                moves.addAll(calculateAvailableMoves(piece));
            }
        }
        for (Move pseudoLegalMove : moves){
            if(king.getCol()== pseudoLegalMove.getCol() && king.getRow()== pseudoLegalMove.getRow()){
                return true;
            }
        }
        return false;
    }
    public Piece getKing(Piece.Color color){
        return board.pieces.stream()
                .filter(piece -> piece.getColor()==color && piece.getType()==KING)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);//king always must be on the board
    }
}
