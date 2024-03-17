package pl.chess.domain;

import java.util.ArrayList;
import java.util.List;

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
    public boolean whiteKingMoved;
    public boolean whiteLeftRookMoved;
    public boolean whiteRightRookMoved;
    public boolean blackKingMoved;
    public boolean blackLeftRookMoved;
    public boolean blackRightRookMoved;
    public Board() {
        this.pieces = new ArrayList<>();
        this.whiteKingMoved = false;
        this.whiteLeftRookMoved = false;
        this.whiteRightRookMoved = false;
        this.blackKingMoved = false;
        this.blackLeftRookMoved = false;
        this.blackRightRookMoved = false;
    }
}
