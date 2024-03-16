package pl.chess.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.chess.domain.Board;
import pl.chess.domain.Piece;
import pl.chess.service.GameService;

import java.util.List;

@RestController
@RequestMapping ("/api/game")
@RequiredArgsConstructor
public class GameController {
    private GameService gameService;
    @GetMapping("/display")
    public List<Piece> display(){
        return new Board().pieces;
    }
}
