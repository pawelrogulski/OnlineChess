package pl.chess.controller;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.support.NullValue;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.chess.domain.Board;
import pl.chess.domain.Player;
import pl.chess.service.AuthenticationService;

import java.util.UUID;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthenticationService authenticationService;
    private Player userBeforeTest;
    private Player enemyUserBeforeTest;
    @BeforeAll
    public void setup(){
        userBeforeTest = new Player( authenticationService.createPlayer("userBeforeTest"),"userBeforeTest");
        enemyUserBeforeTest = new Player(authenticationService.createPlayer("enemyUserBeforeTest"),"enemyUserBeforeTest");
        authenticationService.newMultiGame(new Board(userBeforeTest,enemyUserBeforeTest, Board.GameMode.MULTIPLAYER));
    }

    @Test
    void signUp() throws Exception {
        String username = "user";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(username))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }
    @Test
    void signUpWithTooShortUsername() throws Exception {
        String username = "12";// 2 chars
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(username))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }
    @Test
    void signUpWithTooLongUsername() throws Exception {
        String username = "123456789012345678901";//21 chars
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(username))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    void signInSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signIn")
                        .header("Authorization", userBeforeTest.getUserId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    @Test
    void signInToNonExistingUser() throws Exception {
        Player nonExistingUser = new Player(UUID.randomUUID(),"nonExistingUser");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signIn")
                        .header("Authorization", nonExistingUser.getUserId()))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    void getUsername() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/getUsername")
                        .header("Authorization", userBeforeTest.getUserId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.content().string("userBeforeTest"));
    }

    @Test
    void getEnemyUsername() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/getEnemyUsername")
                        .header("Authorization", userBeforeTest.getUserId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.content().string("enemyUserBeforeTest"));
    }
}