package pl.chess.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Move {
    private int col;
    private int row;
    private Type type;
    public enum Type{
        NORMAL,
        CASTLE,
        EN_PASSANT
    }
}
