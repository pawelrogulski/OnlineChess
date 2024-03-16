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
    public static final int width = 8;
    public static final int height = 8;
    public List<Piece> pieces;

    public Board() {
        this.pieces = new ArrayList<>();
    }
}
