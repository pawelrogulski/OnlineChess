package pl.chess.service;

import org.springframework.stereotype.Service;
import pl.chess.domain.*;

import java.lang.reflect.InvocationTargetException;

import static pl.chess.domain.Piece.Color.*;

@Service
public class GameService {
    public void initializeBoard(Board board) throws Exception {
        Class[] pieces = {Rook.class, Knight.class, Bishop.class, Queen.class, King.class, Bishop.class, Knight.class, Rook.class};
        for(int i=0;i<board.width;i++){
            board.pieces.add((Piece) pieces[i].getConstructor(int.class,int.class, Piece.Color.class).newInstance(i,0,BLACK));
        }
        for(int i=0;i<board.width;i++){
            board.pieces.add(new Pawn(i,1,BLACK));
        }
        for(int i=0;i<board.width;i++){
            board.pieces.add(new Pawn(i,6,WHITE));
        }
        for(int i=0;i<board.width;i++){//reversed order of pieces to inverse king with queen
            board.pieces.add((Piece) pieces[board.width-i-1].getConstructor(int.class,int.class, Piece.Color.class).newInstance(i,7,WHITE));
        }
    }
}
