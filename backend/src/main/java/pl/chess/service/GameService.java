package pl.chess.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.chess.domain.*;


import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

import static pl.chess.domain.Piece.Color.*;
import static pl.chess.domain.Piece.Type.*;
import static pl.chess.domain.Move.Type.*;

@Service
@AllArgsConstructor
public class GameService {
    private final AuthenticationService authenticationService;
    private final ChessEngineService chessEngineService;
    private final NotificationService notificationService;
    public void newSingleGame(UUID playerId){
        authenticationService.newSingleGame(playerId, initializeBoard(playerId));
    }
    public List<Piece> getBoard(UUID playerId){
        return authenticationService.findBoard(playerId).pieces;
    }
    public Board initializeBoard(UUID whitePlayer){
        Board initializedBoard = new Board(authenticationService.validatePlayerCredentials(whitePlayer), authenticationService.getEngineInstance(), Board.GameMode.SINGLEPLAYER);
        return initializePieces(initializedBoard);
    }
    public Board initializeBoard(UUID whitePlayer, UUID blackPlayer){
        Board initializedBoard = new Board(authenticationService.validatePlayerCredentials(whitePlayer), authenticationService.validatePlayerCredentials(blackPlayer), Board.GameMode.MULTIPLAYER);
        return initializePieces(initializedBoard);
    }
    private Board initializePieces(Board board){
        //set pieces position at the start of game
        Piece.Type[] types = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
        for(int i=0;i<8;i++){
            board.pieces.add(new Piece(i,0,WHITE, types[i]));
        }
        for(int i=0;i<8;i++){
            board.pieces.add(new Piece(i,1,WHITE,PAWN));
        }
        for(int i=0;i<8;i++){
            board.pieces.add(new Piece(i,6,BLACK,PAWN));
        }
        for(int i=0;i<8;i++){
            board.pieces.add(new Piece(i,7,BLACK, types[i]));
        }
        return board;
    }
    public Optional<Piece> getPieceAt(int col, int row, List<Piece> pieces){//method for checking pseudo legal moves
        checkInputValues(col,row);//if optional is empty then there is no piece in the cell, if cords out of board then exception
        return pieces.stream()
                .filter(piece -> piece.getCol() == col && piece.getRow() == row)
                .findFirst();
    }
    public Optional<Piece> getPieceAt(int col, int row, UUID playerId){//default method for board
        return getPieceAt(col,row,authenticationService.findBoard(playerId).pieces);
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
    public Board movePiece(int colOrigin, int rowOrigin, int colTarget, int rowTarget, UUID playerId){
        checkInputValues(colOrigin,rowOrigin,colTarget,rowTarget);
        Board board = authenticationService.findBoard(playerId);
        Piece piece = getPieceAt(colOrigin, rowOrigin, playerId)
                .orElseThrow(() -> new IllegalArgumentException("Empty cell selected as piece to move"));
        Move target = calculateLegalMoves(colOrigin, rowOrigin, board)
                .stream()
                .filter(move -> move.getCol()==colTarget && move.getRow()==rowTarget)//is there a move that allows to go there?
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Piece can't move to selected cell"));
        board.enPassantCol=-1;
        board.enPassantRow=-1;//reset en passant
        movePiece(piece, target, board);
        disableCastleCheck(colOrigin,rowOrigin,colTarget,rowTarget, board);
        if(isMate(piece.getColor(), board)){
            return board;
        }
        board.changeTurn();
        if(board.getGameMode()== Board.GameMode.SINGLEPLAYER){
            useEngine(board);
            if(isMate(piece.getColor(), board)){
                return board;
            }
            board.enPassantCol=-1;
            board.enPassantRow=-1;//reset en passant
        }
        return board;
    }
    public void movePiece(Piece piece, Move move, Board board){
        switch (move.getType()){
            case NORMAL -> normalMove(piece,move, board.pieces);
            case DOUBLE_JUMP -> doubleJumpMove(piece,move,board);//set en passant
            case CASTLE -> castleMove(piece,move,board.pieces);
            case EN_PASSANT -> EnPassantMove(piece,move,board.pieces);
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
    public void doubleJumpMove(Piece piece, Move move, Board board){
        removePiece(move.getCol(), move.getRow(), board.pieces);
        piece.setCol(move.getCol());
        piece.setRow(move.getRow());
        board.enPassantCol=piece.getCol();
        board.enPassantRow = piece.getColor()==WHITE ? 2 : 5;
    }
    public void castleMove(Piece king, Move move, List<Piece> pieces){
        int rookCol = move.getCol() == 6 ? 7 : 0;
        int rookRow = move.getRow();
        Piece rook = getPieceAt(rookCol, rookRow, pieces).orElseThrow(() -> new IllegalArgumentException ("Rook not found"));
        king.setCol(move.getCol());
        rook.setCol(3 + 2*rookCol/7);//if on column 0, then 3, if on column 7 then 5
        //rows stays same
    }
    public void EnPassantMove(Piece piece, Move move, List<Piece> pieces){
        Piece capturedPawn = getPieceAt(move.getCol(), piece.getRow(), pieces).orElseThrow(() -> new IllegalArgumentException("Pawn not found"));
        removePiece(capturedPawn.getCol(),capturedPawn.getRow(),pieces);
        piece.setCol(move.getCol());
        piece.setRow(move.getRow());
    }
    public List<Move> calculateLegalMoves(int col, int row, UUID playerId){
        Board board = authenticationService.findBoard(playerId);
        if(!board.getTurn().getUserId().equals(playerId)){
            return new ArrayList<>();//if not player turn then he can't move
        }
        if(getPieceAt(col,row,board.pieces).isEmpty()){
            return new ArrayList<>();//no piece found on given cell
        }
        Player player = getPieceAt(col,row,board.pieces).get().getColor()==WHITE ? board.getWhitePlayer() : board.getBlackPlayer();//if white piece check white player turn
        if(player != board.getTurn()){
            return new ArrayList<>();//white player can't move black piece and so on
        }
        return calculateLegalMoves(col, row, authenticationService.findBoard(playerId));
    }
    public List<Move> calculateLegalMoves(int col, int row, Board board){
        checkInputValues(col, row);
        if(getPieceAt(col,row, board.pieces).isEmpty())
            throw new IllegalArgumentException("No piece found at given cords");
        Piece piece = getPieceAt(col, row, board.pieces).get();
        List<Move> moves = calculateAvailableMoves(piece, board);
        deleteIllegalMoves(piece ,moves, board);
        return moves;
    }
    public List<Move> calculateAvailableMoves(Piece piece, Board board){
        return switch (piece.getType()){
                    case BISHOP -> calculateAvailableBishopMoves(piece, board.pieces);
                    case KING -> calculateAvailableKingMoves(piece, board);
                    case KNIGHT -> calculateAvailableKnightMoves(piece, board.pieces);
                    case PAWN -> calculateAvailablePawnMoves(piece, board);
                    case QUEEN -> calculateAvailableQueenMoves(piece, board.pieces);
                    case ROOK -> calculateAvailableRookMoves(piece, board.pieces);
        };
    }
    public List<Move> calculateAvailableBishopMoves(Piece piece, List<Piece> pieces){
        List<Move> moves = new ArrayList<>();
        /** bishop "x" moves can be calculated by adding same number (range 1 to 7) to col and row in 4 cases: both positive, 
         * positive and negative, negative and positive, both negative
         **/
        for(int j=-1;j<=1;j=j+2){
            for(int k=-1;k<=1;k=k+2){
                try{
                    for(int i=1;i<=7;i++){//2 loops to move in 2 dimensions, 3rd loop to move more than 1 cell
                        if(getPieceAt(piece.getCol()+i*j,piece.getRow()+i*k, pieces).isEmpty()){ //2nd part can be null and throw exception
                            moves.add(new Move(piece.getCol()+i*j,piece.getRow()+i*k, NORMAL));//empty cell
                        }
                        else if(getPieceAt(piece.getCol()+i*j,piece.getRow()+i*k, pieces).get().getColor()!=piece.getColor()){
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
        return moves;
    }
    public List<Move> calculateAvailableKingMoves(Piece piece, Board board){
        List<Move> moves = new ArrayList<>();
        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                try{
                    if(getPieceAt(i+piece.getCol(),j+piece.getRow(), board.pieces).isEmpty() || getPieceAt(i+piece.getCol(),j+piece.getRow(), board.pieces).get().getColor()!=piece.getColor()){ //2nd part can be null and throw exception
                        moves.add(new Move(i+piece.getCol(),j+piece.getRow(), NORMAL));
                    }
                }catch (Exception e){}
            }
        }
        return calculateCastleMoves(moves, piece, board);
    }
    public List<Move> calculateCastleMoves(List<Move> moves, Piece piece, Board board){
        int side = piece.getColor()== WHITE ? 0 : 7;
        boolean king = piece.getColor()== WHITE ? board.whiteKingMoved : board.blackKingMoved;
        boolean rightRook = piece.getColor()== WHITE ? board.whiteRightRookMoved : board.blackRightRookMoved;
        boolean leftRook = piece.getColor()== WHITE ? board.whiteLeftRookMoved : board.blackLeftRookMoved;
        if(!king){
            if(getPieceAt(5,side, board.pieces).isEmpty() && getPieceAt(6,side, board.pieces).isEmpty() && !rightRook){
                moves.add(new Move(6,side,CASTLE));
            }
            if(getPieceAt(1,side, board.pieces).isEmpty() && getPieceAt(2,side, board.pieces).isEmpty() && getPieceAt(3,side, board.pieces).isEmpty() && !leftRook){
                moves.add(new Move(2,side,CASTLE));
            }
        }
        return moves;
    }
    public List<Move> calculateAvailableKnightMoves(Piece piece, List<Piece> pieces){
        List<Move> moves = new ArrayList<>();
        for(int i=-2;i<=2;i++){
            for(int j=-2;j<=2;j++){
                if(!(i==0 || j==0 || Math.abs(i)==Math.abs(j))) {//not "L" move
                    try{
                        if(getPieceAt(i+piece.getCol(),j+piece.getRow(), pieces).isEmpty() || getPieceAt(i+piece.getCol(),j+piece.getRow(), pieces).get().getColor()!=piece.getColor()){ //2nd part can be null and throw exception
                            moves.add(new Move(i+piece.getCol(),j+piece.getRow(), NORMAL));
                        }
                    }catch (Exception e){}
                }
            }
        }
        return moves;
    }
    public List<Move> calculateAvailablePawnMoves(Piece piece, Board board){
        List<Move> moves = new ArrayList<>();
        int side = piece.getColor()== WHITE ? 1 : -1;
        if(getPieceAt(piece.getCol(),piece.getRow()+side, board.pieces).isEmpty()){
            moves.add(new Move(piece.getCol(), piece.getRow()+side, NORMAL));//move 1 cell forward
            calculateAvailableDoubleJump(moves, piece, board.pieces);
        }
        for(int i=-1;i<=1;i=i+2){
            try{
                if(getPieceAt(piece.getCol()+i,piece.getRow()+side, board.pieces).isPresent() && getPieceAt(piece.getCol()+i,piece.getRow()+side, board.pieces).get().getColor()!=piece.getColor()){
                    moves.add(new Move(piece.getCol()+i,piece.getRow()+side,NORMAL));//move and capture
                }
            }catch (Exception e){}
        }
        return calculateAvailableEnPassant(moves, piece, board);
    }
    public List<Move> calculateAvailableDoubleJump(List<Move> moves, Piece piece, List<Piece> pieces){
        int side = piece.getColor()== WHITE ? 5 : 0;
        if(piece.getRow()+side==6){
            moves.add(new Move(piece.getCol(), piece.getRow()-2+4*(side/5), DOUBLE_JUMP));//if white add 2, if black substract 2
        }
        return moves;
    }
    public List<Move> calculateAvailableEnPassant(List<Move> moves, Piece piece, Board board){
        if(board.enPassantCol==-1) 
            return moves;//not available
        int side = piece.getColor()== WHITE ? 4 : 3;
        if(piece.getRow()==side && Math.abs(piece.getCol()-board.enPassantCol)==1){// "v" or "^" move
            moves.add(new Move(board.enPassantCol, board.enPassantRow, EN_PASSANT));
        }
        return moves;
    }
    public List<Move> calculateAvailableQueenMoves(Piece piece, List<Piece> pieces){
        //Queen moves are sum of bishop and rook moves, without castle
        List<Move> moves = calculateAvailableBishopMoves(piece, pieces);
        moves.addAll(calculateAvailableRookMoves(piece, pieces));
        Iterator<Move> movesWithoutCastle = moves.iterator();
        while(movesWithoutCastle.hasNext()){
            if(movesWithoutCastle.next().getType()==CASTLE){
                movesWithoutCastle.remove();
            }
        }
        return moves;
    }
    public List<Move> calculateAvailableRookMoves(Piece piece, List<Piece> pieces){
        List<Move> moves = new ArrayList<>();
        /** rook "+" moves can be calculated by adding values 1 to 7 to col, 1 to 7 to row, -1 to -7 to col, -1 to -7 to row
         * first 2 loops are for moving in 2 dimensions, then moves in 1 line are added till meeting piece (break) or out of board (catch)
         */
        for(int k=-1;k<=1;k=k+2){//1- up and right, -1 - down and left
            for(int j=0;j<=1;j++){//"switch" - if 0 - cols, if 1 - rows
                try{
                    for(int i=1;i<=7;i++){//2 loops to move in 2 dimensions, 3rd loop to move more than 1 cell
                        if(getPieceAt(piece.getCol()+i*(k*(j-1)),piece.getRow()+i*k*j, pieces).isEmpty()){ //2nd part can be null and throw exception
                            moves.add(new Move(piece.getCol()+i*(k*(j-1)),piece.getRow()+i*k*j, NORMAL));//empty cell
                        }
                        else if(getPieceAt(piece.getCol()+i*(k*(j-1)),piece.getRow()+i*k*j, pieces).get().getColor()!=piece.getColor()){
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
    public List<Move> deleteIllegalMoves(Piece piece, List<Move> moves, Board board){
        Iterator<Move> pseudoLegalMove = moves.iterator();//iterator for removing in "foreach"
        while(pseudoLegalMove.hasNext()){
            //clone board and pieces
            List<Piece> clonedPieces = new ArrayList<>(board.pieces.size());
            board.pieces
                    .stream()
                    .forEach(originPiece -> clonedPieces.add(new Piece(originPiece)));
            Board clonedBoard = new Board(clonedPieces);
            //get reference to cloned piece on origin piece cords
            Piece clonedPiece = getPieceAt(piece.getCol(), piece.getRow(), clonedBoard.pieces).orElseThrow(IllegalArgumentException::new);
            //make pseudo legal move
            movePiece(clonedPiece,pseudoLegalMove.next(),clonedBoard);
            //if king is checked after move then move was illegal
            if(isCheck(clonedPiece.getColor(), clonedBoard)){
                pseudoLegalMove.remove();
            }
        }
        return moves;
    }
    public boolean isCheck(Piece.Color kingColor, Board board){
        Piece king = getKing(kingColor, board.pieces);
        List<Move> moves = new ArrayList<>();
        for (Piece piece : board.pieces){
            if(piece.getColor()!=kingColor){
                moves.addAll(calculateAvailableMoves(piece, board));
            }
        }
        for (Move pseudoLegalMove : moves){
            if(king.getCol()== pseudoLegalMove.getCol() && king.getRow()== pseudoLegalMove.getRow()){
                return true;
            }
        }
        return false;
    }
    public boolean isMate(Piece.Color color, Board board){
        List<Move> moves = new ArrayList<>();
        String score = "";
        board.pieces//get all moves of enemy
                .stream()
                .filter(piece -> piece.getColor()!=color)
                .forEach(piece -> moves.addAll(calculateLegalMoves(piece.getCol(),piece.getRow(),board)));
        if(moves.size()>0){
            return false;
        }
        Piece.Color enemyColor = color==WHITE ? BLACK : WHITE;
        if(isCheck(enemyColor,board)){
            score = enemyColor==WHITE ? "BLACK_WON" : "WHITE_WON";
        }
        else{
            score = "DRAW";
        }
        notificationService.sendToEmitter(score,board.getWhitePlayer().getUserId());
        if(board.getGameMode()== Board.GameMode.MULTIPLAYER){
            notificationService.sendToEmitter(score,board.getBlackPlayer().getUserId());
        }
        authenticationService.deleteSession(board);
        return true;
    }
    public Piece getKing(Piece.Color color, List<Piece> pieces){
        return pieces.stream()
                .filter(piece -> piece.getColor()==color && piece.getType()==KING)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);//king always must be on the board
    }
    public void disableCastleCheck(int colOrigin, int rowOrigin, int colTarget, int rowTarget, Board board){
        if(colOrigin ==0 && rowOrigin ==0){
            board.whiteLeftRookMoved=true;
        }
        if(colTarget ==0 && rowTarget ==0){
            board.whiteLeftRookMoved=true;
        }
        if(colOrigin ==7 && rowOrigin ==0){
            board.whiteRightRookMoved=true;
        }
        if(colTarget ==7 && rowTarget ==0){
            board.whiteRightRookMoved=true;
        }
        if(colOrigin ==0 && rowOrigin ==7){
            board.blackLeftRookMoved=true;
        }
        if(colTarget ==0 && rowTarget ==7){
            board.blackLeftRookMoved=true;
        }
        if(colOrigin ==7 && rowOrigin ==7){
            board.blackRightRookMoved=true;
        }
        if(colTarget ==7 && rowTarget ==7){
            board.blackRightRookMoved=true;
        }
        if(colOrigin ==4){
            if(rowOrigin ==0){
                board.whiteKingMoved=true;
            }
            if(rowOrigin ==7){
                board.blackKingMoved=true;
            }
        }
    }
    private void useEngine(Board board){
        Piece.Color color = board.getTurn()==board.getWhitePlayer() ? WHITE : BLACK;
        while(true){
            Piece selectedPiece = chessEngineService.selectRandomPiece(
                    board.pieces.stream()
                            .filter(piece -> piece.getColor()==color)
                            .collect(Collectors.toList()));
            List<Move> moves = calculateLegalMoves(selectedPiece.getCol(), selectedPiece.getRow(), board);
            if(!moves.isEmpty()){
                Move move = chessEngineService.selectRandomMove(moves);
                movePiece(selectedPiece, move, board);
                disableCastleCheck(selectedPiece.getCol(), selectedPiece.getRow(), move.getCol(), move.getRow(), board);
                break;
            }
        }
        board.changeTurn();
    }
}
