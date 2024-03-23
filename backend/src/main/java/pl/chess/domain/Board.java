package pl.chess.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *   0 1 2 3 4 5 6 7
 * 0
 * 1
 * 2
 * 3
 * 4
 * 5
 * 6
 * 7
 * **/
public class Board {
    public List<Piece> pieces;
    private GameMode gameMode;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player turn;
    public boolean whiteKingMoved;
    public boolean whiteLeftRookMoved;
    public boolean whiteRightRookMoved;
    public boolean blackKingMoved;
    public boolean blackLeftRookMoved;
    public boolean blackRightRookMoved;
    public int enPassantCol;
    public int enPassantRow;
    public enum GameMode{
        SINGLEPLAYER,
        MULTIPLAYER
    }

    public Board(Player whitePlayer) {
        this.pieces = new ArrayList<>();
        this.whitePlayer = whitePlayer;
        this.gameMode = GameMode.SINGLEPLAYER;
        this.turn = whitePlayer;
        this.whiteKingMoved = false;
        this.whiteLeftRookMoved = false;
        this.whiteRightRookMoved = false;
        this.blackKingMoved = false;
        this.blackLeftRookMoved = false;
        this.blackRightRookMoved = false;
        this.enPassantCol = -1;
        this.enPassantRow = -1;//if -1, it's not available
    }
    public Board(Player whitePlayer, Player blackPlayer) {
        this.pieces = new ArrayList<>();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.gameMode = GameMode.MULTIPLAYER;
        this.turn = whitePlayer;
        this.whiteKingMoved = false;
        this.whiteLeftRookMoved = false;
        this.whiteRightRookMoved = false;
        this.blackKingMoved = false;
        this.blackLeftRookMoved = false;
        this.blackRightRookMoved = false;
        this.enPassantCol = -1;
        this.enPassantRow = -1;//if -1, it's not available
    }
    public Board(List<Piece> pieces){
        this.pieces=pieces;
    }
    public Player getTurn(){
        return this.turn;
    }
    public Player changeTurn(){
        this.turn = getTurn().equals(whitePlayer) ? blackPlayer : whitePlayer;
        return this.turn;
    }
    public Player getWhitePlayer(){
        return this.whitePlayer;
    }
    public Player getBlackPlayer(){
        return this.blackPlayer;
    }
}
