package pl.chess.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import pl.chess.domain.Move;
import pl.chess.domain.Piece;
import pl.chess.service.GameService;
import java.util.List;

@RestController
@RequestMapping ("/api/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    @GetMapping("/display")
    public List<Piece> display(){
        return gameService.getBoard();
    }
//    @PostMapping("/move")
//    public List<Piece> move(@RequestBody CordsDTO cordsDTO){
//        gameService.movePiece(cordsDTO.colOrigin,cordsDTO.rowOrigin,cordsDTO.colTarget,cordsDTO.rowTarget);
//        return gameService.getBoard();
//    }
    @GetMapping("/checkMoves")
    public List<Move> checkMoves(@RequestBody CordsDTO cordsDTO){
        return gameService.calculateLegalMoves(cordsDTO.colOrigin,cordsDTO.rowOrigin);
    }

    @Data
    static class CordsDTO{
        private int colOrigin;
        private int rowOrigin;
        private int colTarget;
        private int rowTarget;
    }
}
