package pl.chess.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Piece {
    private int col;
    private int row;
    private Color color;
    private Type type;
    public enum Color{
        WHITE,
        BLACK
    }
    public enum Type {
        BISHOP,
        KING,
        KNIGHT,
        PAWN,
        QUEEN,
        ROOK
    }
    public Piece(Piece piece){//clone
        this.col= piece.col;
        this.row= piece.row;
        this.color=piece.color;
        this.type=piece.type;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Color getColor() {
        return color;
    }
    public Type getType() {
        return type;
    }
}
