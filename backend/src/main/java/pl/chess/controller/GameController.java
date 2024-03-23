package pl.chess.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.chess.domain.Move;
import pl.chess.domain.Piece;
import pl.chess.service.GameService;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping ("/api/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    @GetMapping("/display")
    public List<Piece> display(@RequestHeader("Authorization") String playerId){
        return gameService.getBoard(UUID.fromString(playerId));
    }
    @PostMapping("/move")
    public List<Piece> move(@RequestHeader("Authorization") String playerId, @RequestBody CordsDTO cordsDTO){
        return gameService.movePiece(cordsDTO.colOrigin,cordsDTO.rowOrigin,cordsDTO.colTarget,cordsDTO.rowTarget, UUID.fromString(playerId)).pieces;
    }
    @GetMapping("/checkMoves")
    public ResponseEntity<List<Move>> checkMoves(@RequestHeader("Authorization") String playerId, @RequestParam int col, @RequestParam int row){
        return ResponseEntity.ok(gameService.calculateLegalMoves(col,row, UUID.fromString(playerId)));
    }
    @PostMapping("/newSingleGame")
    public boolean newSingleGame(@RequestHeader("Authorization") String playerId){
        gameService.newSingleGame(UUID.fromString(playerId));
        return true;
    }

    @Data
    static class CordsDTO{
        private int colOrigin;
        private int rowOrigin;
        private int colTarget;
        private int rowTarget;
    }
}
