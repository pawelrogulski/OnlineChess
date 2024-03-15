package pl.chess.dev;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.chess.domain.Board;
import pl.chess.service.GameService;
@Component
@AllArgsConstructor
public class dev {
    private GameService gameService;
    public void display(Board board){
        for(int i=0;i<board.height;i++){
            for (int j=0;j<board.width;j++){
                System.out.print(" ");
                try {
                    switch (board.getPieces()[i][j].getClass().getName()) {
                        case "pl.chess.domain.Rook":
                            System.out.print("R");
                            break;
                        case "pl.chess.domain.Knight":
                            System.out.print("k");
                            break;
                        case "pl.chess.domain.Bishop":
                            System.out.print("B");
                            break;
                        case "pl.chess.domain.Queen":
                            System.out.print("Q");
                            break;
                        case "pl.chess.domain.King":
                            System.out.print("K");
                            break;
                        case "pl.chess.domain.Pawn":
                            System.out.print("P");
                            break;
                    }
                }catch (Exception e){
                    System.out.print("_");
                }

            }
            System.out.println();
        }
    }

    @PostConstruct
    public void init(){
        Board board = new Board();
        gameService.initializeBoard(board);
        display(board);
    }
}
