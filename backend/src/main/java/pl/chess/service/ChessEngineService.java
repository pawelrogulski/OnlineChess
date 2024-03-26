package pl.chess.service;

import org.springframework.stereotype.Service;
import pl.chess.domain.Move;
import pl.chess.domain.Piece;

import java.util.List;
import java.util.Random;

@Service
public class ChessEngineService {
    Random random = new Random();
    public Piece selectRandomPiece(List<Piece> pieces){
        int pieceIndex = random.nextInt(pieces.size());
        return pieces.get(pieceIndex);
    }
    public Move selectRandomMove(List<Move> moves){
        int moveIndex = random.nextInt(moves.size());
        return moves.get(moveIndex);
    }
}
