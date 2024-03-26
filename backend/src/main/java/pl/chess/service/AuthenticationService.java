package pl.chess.service;

import org.springframework.stereotype.Service;
import pl.chess.domain.Board;
import pl.chess.domain.Player;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationService {
    private Player engine = new Player(UUID.randomUUID(), "BOT");
    private Map<UUID, Board> gameSessions = new HashMap<>();
    private Map<UUID, String> players = new HashMap<>(Map.of(engine.getUserId(),engine.getUsername()));

    public boolean playerExists(UUID playerId){
        if(players.containsKey(playerId)){
            return true;
        }
        return false;
    }

    public Player getEngineInstance(){
        return engine;
    }
    public Board findBoard(UUID playerId){
        if(!players.containsKey(playerId)){
            throw new IllegalArgumentException("Player not found");
        }
        if(!gameSessions.containsKey(playerId)){
            throw new IllegalArgumentException("Player not in game");
        }
        return gameSessions.get(playerId);
    }
    private void validateUsername(String username){
        if(username.isBlank()){
            throw new IllegalArgumentException("Username can't be blank");
        }
        if(username.length()<3){
            throw new IllegalArgumentException("Username too short");
        }
        if(username.length()>20){
            throw new IllegalArgumentException("Username too long");
        }
    }
    public Player validatePlayerCredentials(UUID playerId){
        Player player = new Player();
        if(players.containsKey(playerId)){
            player.setUsername(players.get(playerId));
            player.setUserId(playerId);
            return player;
        }
        throw new IllegalArgumentException("User not found");
    }
    public UUID createPlayer(String username){
        validateUsername(username);
        if(players.containsValue(username)){
            throw new IllegalArgumentException("Username in use");
        }
        UUID playerId = UUID.randomUUID();
        while(true){
            if(players.containsKey(playerId)){//if id in use
                playerId = UUID.randomUUID();
            }
            else{
                break;
            }
        }
        players.put(playerId, username);
        return playerId;
    }
    public void newSingleGame(Board board){
        gameSessions.put(board.getWhitePlayer().getUserId(), board);
    }
    public void deleteSession(Board board){
        gameSessions.remove(board.getWhitePlayer().getUserId());
        if(board.getGameMode()== Board.GameMode.MULTIPLAYER){
            gameSessions.remove(board.getBlackPlayer().getUserId());
        }
    }
    public void newMultiGame(Board board){
        gameSessions.put(board.getWhitePlayer().getUserId(), board);
        gameSessions.put(board.getBlackPlayer().getUserId(), board);
    }

    public String getUsername(UUID playerId) {
        return players.get(playerId);
    }
    public String getEnemyUsername(UUID playerId) {
        Board board = gameSessions.get(playerId);
        return !board.getWhitePlayer().getUserId().equals(playerId) ? board.getWhitePlayer().getUsername() : board.getBlackPlayer().getUsername();
    }
}
