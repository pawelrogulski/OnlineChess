package pl.chess.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.chess.domain.Board;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchmakingService {
    private final AuthenticationService authenticationService;
    private final NotificationService notificationService;
    private final GameService gameService;
    private Queue<UUID> multiplayerQueue = new PriorityQueue<>();

    public boolean addPlayerToQueue(UUID playerId) throws InterruptedException {
        if(!authenticationService.playerExists(playerId)){
            return false;
        }
        if(!multiplayerQueue.contains(playerId)){
            multiplayerQueue.add(playerId);
        }
        if(multiplayerQueue.size()>1){
            sendStartMultiplayerGameNotification();
        }
        return true;
    }
    private void sendStartMultiplayerGameNotification() throws InterruptedException {
        Thread.sleep(100);
        UUID whitePlayerId = multiplayerQueue.peek();
        multiplayerQueue.remove();
        UUID blackPlayerId = multiplayerQueue.peek();
        multiplayerQueue.remove();
        Board board = gameService.initializeBoard(whitePlayerId,blackPlayerId);
        authenticationService.newMultiGame(board);
        notificationService.sendToEmitter("WHITE" ,whitePlayerId);
        notificationService.sendToEmitter("BLACK" ,blackPlayerId);
    }
}
