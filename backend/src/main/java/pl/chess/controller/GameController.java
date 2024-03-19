package pl.chess.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Move>> checkMoves(@RequestParam int col, @RequestParam int row){
        System.out.println("xd");
        return ResponseEntity.ok(gameService.calculateLegalMoves(col,row));
    }

    @Data
    static class CordsDTO{
        private int colOrigin;
        private int rowOrigin;
        private int colTarget;
        private int rowTarget;
    }
}
