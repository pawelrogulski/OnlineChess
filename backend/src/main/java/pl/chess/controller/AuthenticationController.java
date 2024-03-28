package pl.chess.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.chess.domain.Player;
import pl.chess.service.AuthenticationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/signUp")
    public ResponseEntity<UUID> signUp(@RequestBody String username){
        return ResponseEntity.ok(authenticationService.createPlayer(username));
    }
    @PostMapping("/signIn")
    public Player signIn(@RequestHeader("Authorization") String playerId){
        return authenticationService.validatePlayerCredentials(UUID.fromString(playerId));
    }
    @GetMapping("/getUsername")
    public ResponseEntity<String> getUsername(@RequestHeader("Authorization") String playerId){
        return ResponseEntity.ok(authenticationService.getUsername(UUID.fromString(playerId)));
    }
    @GetMapping("/getEnemyUsername")
    public ResponseEntity<String> getEnemyUsername(@RequestHeader("Authorization") String playerId){
        return ResponseEntity.ok(authenticationService.getEnemyUsername(UUID.fromString(playerId)));
    }
}
