package pl.chess.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import pl.chess.domain.Board;
import pl.chess.domain.Move;
import pl.chess.domain.Piece;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameServiceTest {
    @Autowired
    private GameService gameService;
    @Test
    void testNegativeInput(){
        assertThrows(IllegalArgumentException.class, () -> gameService.calculateLegalMoves(-1,-1,new Board(null)));
    }
    @Test
    void testTooLargeInput(){
        assertThrows(IllegalArgumentException.class, () -> gameService.calculateLegalMoves(8,8,new Board(null)));
    }

    @Test
    void testNoPieceFound() {
        List<Piece> pieces = new ArrayList<>();
        pieces.add(new Piece(0,0, Piece.Color.BLACK, Piece.Type.KING));
        pieces.add(new Piece(7,7, Piece.Color.WHITE, Piece.Type.KING));
        Board board = new Board(pieces);
        assertThrows(RuntimeException.class, () -> gameService.calculateLegalMoves(5,5,board));
    }

    boolean compareListsOfPieces(List<Move> receivedMoves, List<Move> expectedMoves ){
        if(receivedMoves.isEmpty() && expectedMoves.isEmpty()) return true;
        if(receivedMoves.size()!=expectedMoves.size()) return false;
        for(Move expectedMove : expectedMoves){
            boolean containsFlag = false;
            for(Move move : receivedMoves){
                if(move.equals(expectedMove)){
                    containsFlag = true;
                    break;
                }
            }
            if(!containsFlag) return false;
        }
        return true;
    }

    @Test
    void testPawnDoubleJump() {
        List<Piece> pieces = new ArrayList<>();
        pieces.add(new Piece(0,0, Piece.Color.BLACK, Piece.Type.KING));
        pieces.add(new Piece(7,7, Piece.Color.WHITE, Piece.Type.KING));
        pieces.add(new Piece(0,1, Piece.Color.WHITE, Piece.Type.PAWN));
        Board board = new Board(pieces);
        List<Move> expectedMoves = new ArrayList<>();
        expectedMoves.add(new Move(0,2, Move.Type.NORMAL));
        expectedMoves.add(new Move(0,3, Move.Type.DOUBLE_JUMP));
        List<Move> moves = gameService.calculateLegalMoves(0,1,board);
        assertTrue(compareListsOfPieces(moves,expectedMoves));

    }
    @Test
    void testKnight() {
        List<Piece> pieces = new ArrayList<>();
        pieces.add(new Piece(0,0, Piece.Color.BLACK, Piece.Type.KING));
        pieces.add(new Piece(7,7, Piece.Color.WHITE, Piece.Type.KING));
        pieces.add(new Piece(3,3, Piece.Color.WHITE, Piece.Type.KNIGHT));
        Board board = new Board(pieces);
        List<Move> expectedMoves = new ArrayList<>();
        expectedMoves.add(new Move(1,2, Move.Type.NORMAL));
        expectedMoves.add(new Move(1,4, Move.Type.NORMAL));
        expectedMoves.add(new Move(2,1, Move.Type.NORMAL));
        expectedMoves.add(new Move(2,5, Move.Type.NORMAL));
        expectedMoves.add(new Move(4,1, Move.Type.NORMAL));
        expectedMoves.add(new Move(4,5, Move.Type.NORMAL));
        expectedMoves.add(new Move(5,2, Move.Type.NORMAL));
        expectedMoves.add(new Move(5,4, Move.Type.NORMAL));
        List<Move> moves = gameService.calculateLegalMoves(3,3, board);
        assertTrue(compareListsOfPieces(moves,expectedMoves));

    }
    @Test
    void testRook() {
        List<Piece> pieces = new ArrayList<>();
        pieces.add(new Piece(0,0, Piece.Color.BLACK, Piece.Type.KING));
        pieces.add(new Piece(7,7, Piece.Color.WHITE, Piece.Type.KING));
        pieces.add(new Piece(3,3, Piece.Color.WHITE, Piece.Type.ROOK));
        Board board = new Board(pieces);
        List<Move> expectedMoves = new ArrayList<>();
        expectedMoves.add(new Move(0,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(1,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(2,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(4,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(5,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(6,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(7,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,0, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,1, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,2, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,4, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,5, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,6, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,7, Move.Type.NORMAL));
        List<Move> moves = gameService.calculateLegalMoves(3,3, board);
        assertTrue(compareListsOfPieces(moves,expectedMoves));
    }
    @Test
    void testBishop() {
        List<Piece> pieces = new ArrayList<>();
        pieces.add(new Piece(1,0, Piece.Color.BLACK, Piece.Type.KING));
        pieces.add(new Piece(1,7, Piece.Color.WHITE, Piece.Type.KING));
        pieces.add(new Piece(3,3, Piece.Color.WHITE, Piece.Type.BISHOP));
        Board board = new Board(pieces);
        List<Move> expectedMoves = new ArrayList<>();
        expectedMoves.add(new Move(0,0, Move.Type.NORMAL));
        expectedMoves.add(new Move(1,1, Move.Type.NORMAL));
        expectedMoves.add(new Move(2,2, Move.Type.NORMAL));
        expectedMoves.add(new Move(4,4, Move.Type.NORMAL));
        expectedMoves.add(new Move(5,5, Move.Type.NORMAL));
        expectedMoves.add(new Move(6,6, Move.Type.NORMAL));
        expectedMoves.add(new Move(7,7, Move.Type.NORMAL));
        expectedMoves.add(new Move(0,6, Move.Type.NORMAL));
        expectedMoves.add(new Move(1,5, Move.Type.NORMAL));
        expectedMoves.add(new Move(2,4, Move.Type.NORMAL));
        expectedMoves.add(new Move(4,2, Move.Type.NORMAL));
        expectedMoves.add(new Move(5,1, Move.Type.NORMAL));
        expectedMoves.add(new Move(6,0, Move.Type.NORMAL));
        List<Move> moves = gameService.calculateLegalMoves(3,3, board);
        assertTrue(compareListsOfPieces(moves,expectedMoves));
    }
    @Test
    void testQueen() {
        List<Piece> pieces = new ArrayList<>();
        pieces.add(new Piece(1,0, Piece.Color.BLACK, Piece.Type.KING));
        pieces.add(new Piece(1,7, Piece.Color.WHITE, Piece.Type.KING));
        pieces.add(new Piece(3,3, Piece.Color.WHITE, Piece.Type.QUEEN));
        Board board = new Board(pieces);
        List<Move> expectedMoves = new ArrayList<>();
        expectedMoves.add(new Move(0,0, Move.Type.NORMAL));
        expectedMoves.add(new Move(1,1, Move.Type.NORMAL));
        expectedMoves.add(new Move(2,2, Move.Type.NORMAL));
        expectedMoves.add(new Move(4,4, Move.Type.NORMAL));
        expectedMoves.add(new Move(5,5, Move.Type.NORMAL));
        expectedMoves.add(new Move(6,6, Move.Type.NORMAL));
        expectedMoves.add(new Move(7,7, Move.Type.NORMAL));
        expectedMoves.add(new Move(0,6, Move.Type.NORMAL));
        expectedMoves.add(new Move(1,5, Move.Type.NORMAL));
        expectedMoves.add(new Move(2,4, Move.Type.NORMAL));
        expectedMoves.add(new Move(4,2, Move.Type.NORMAL));
        expectedMoves.add(new Move(5,1, Move.Type.NORMAL));
        expectedMoves.add(new Move(6,0, Move.Type.NORMAL));
        expectedMoves.add(new Move(0,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(1,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(2,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(4,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(5,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(6,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(7,3, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,0, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,1, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,2, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,4, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,5, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,6, Move.Type.NORMAL));
        expectedMoves.add(new Move(3,7, Move.Type.NORMAL));
        List<Move> moves = gameService.calculateLegalMoves(3,3, board);
        assertTrue(compareListsOfPieces(moves,expectedMoves));
    }
}