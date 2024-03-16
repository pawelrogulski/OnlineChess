package pl.chess.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Piece {
    private int col;
    private int row;
    private Color color;
    public enum Color{
        WHITE,
        BLACK
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
}
