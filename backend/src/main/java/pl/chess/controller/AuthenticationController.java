package pl.chess.controller;

import lombok.RequiredArgsConstructor;
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
    public UUID signUp(@RequestBody String username){
        return authenticationService.createPlayer(username);
    }
    @PostMapping("/signIn")
    public Player signIn(@RequestHeader("Authorization") String playerId){
        return authenticationService.validatePlayerCredentials(UUID.fromString(playerId));
    }
}
