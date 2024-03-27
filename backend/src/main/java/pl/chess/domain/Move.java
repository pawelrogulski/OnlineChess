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
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return col == move.col &&
                row == move.row &&
                type == move.type;
    }
}
