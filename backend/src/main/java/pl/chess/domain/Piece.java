package pl.chess.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Piece {
    private Color color;
    public enum Color{
        WHITE,
        BLACK
    }
}
