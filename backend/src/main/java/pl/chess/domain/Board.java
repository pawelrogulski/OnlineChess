package pl.chess.domain;

public class Board {
    public static final int width = 8;
    public static final int height = 8;
    private Piece pieces[][];

    public Board() {
        this.pieces = new Piece[height][width];
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    public void setPieces(Piece[][] pieces) {
        this.pieces = pieces;
    }
}
