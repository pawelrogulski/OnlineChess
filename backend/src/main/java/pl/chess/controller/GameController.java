package pl.chess.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.chess.domain.Board;
import pl.chess.domain.Piece;
import pl.chess.service.GameService;

import java.util.ArrayList;
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
    @PostMapping("/move")
    public List<Piece> move(@RequestBody CordsDTO cordsDTO){
        gameService.movePiece(cordsDTO.colOrigin,cordsDTO.rowOrigin,cordsDTO.colTarget,cordsDTO.rowTarget);
        return gameService.getBoard();
    }

    @Data
    static class CordsDTO{
        private int colOrigin;
        private int rowOrigin;
        private int colTarget;
        private int rowTarget;
    }
}
