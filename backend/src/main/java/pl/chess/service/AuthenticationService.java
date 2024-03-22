package pl.chess.service;

import org.springframework.stereotype.Service;
import pl.chess.domain.Board;
import pl.chess.domain.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationService {
    private Map<UUID, Board> gameSessions = new HashMap<>();
    private Map<UUID, String> players = new HashMap<>();
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
        }
        return player;
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
}
