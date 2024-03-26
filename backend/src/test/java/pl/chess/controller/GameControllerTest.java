package pl.chess.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.chess.domain.Board;
import pl.chess.domain.Player;
import pl.chess.service.AuthenticationService;
import pl.chess.service.GameService;
import pl.chess.service.MatchmakingService;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GameService gameService;
    @Autowired
    private MatchmakingService matchmakingService;
    @Autowired
    private AuthenticationService authenticationService;
    private Player whitePlayer;
    @BeforeAll
    public void setup(){
        whitePlayer = new Player( authenticationService.createPlayer("userBeforeTest"),"userBeforeTest");
    }
    @BeforeEach
    public void setupEach(){
        authenticationService.newMultiGame(gameService.initializeBoard(whitePlayer.getUserId() ));
    }
    @AfterEach
    public void resetEach(){
        authenticationService.deleteSession(authenticationService.findBoard(whitePlayer.getUserId()));
    }


    @Test
    void displayNewBoardTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/game/display")
                        .header("Authorization", whitePlayer.getUserId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasSize(32)));
    }

    @Test
    void movePawn() throws Exception {
        String requestBody = "{\"colOrigin\": 0, \"rowOrigin\": 1, \"colTarget\": 0, \"rowTarget\": 2}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/game/move")
                        .header("Authorization", whitePlayer.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasSize(32)));
    }

    @Test
    void checkMoves() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/game/checkMoves")
                        .header("Authorization", whitePlayer.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("col", String.valueOf(0))
                        .param("row", String.valueOf(1)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasSize(2)));
    }

    @Test
    void newSingleGame() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/game/newSingleGame")
                        .header("Authorization", whitePlayer.getUserId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}