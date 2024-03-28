package pl.chess.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import pl.chess.domain.Player;
import pl.chess.service.AuthenticationService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationServiceTest {
	@Autowired
	private AuthenticationService authenticationService;
	@Test
	public void findNonExistingBoard(){
		Player player = new Player(UUID.randomUUID(),"NonExistingPlayer");
		assertThrows(RuntimeException.class, () -> authenticationService.findBoard(player.getUserId()));
	}
	@Test
	public void findNotInGamePlayer(){
		UUID playerId = authenticationService.createPlayer("NotInGamePlayer");
		assertThrows(RuntimeException.class, () -> authenticationService.findBoard(playerId));
	}
	@Test
	public void validateNonExistingPlayerCredentials(){
		Player player = new Player(UUID.randomUUID(),"NonExistingPlayer");
		assertThrows(RuntimeException.class, () -> authenticationService.validatePlayerCredentials(player.getUserId()));
	}
	@Test
	public void createPlayerWithUsernameInUse(){
		authenticationService.createPlayer("Player");
		assertThrows(RuntimeException.class, () -> authenticationService.createPlayer("Player"));
	}

}
