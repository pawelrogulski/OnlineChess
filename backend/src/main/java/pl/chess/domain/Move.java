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
        DOUBLE_JUMP,
        CASTLE,
        EN_PASSANT
    }
}
