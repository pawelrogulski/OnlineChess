package pl.chess.controller;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.chess.service.NotificationService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationControllerTest {
    TestRestTemplate testRestTemplate = new TestRestTemplate();
    @LocalServerPort
    private int port;

    @Test
    void testGetNotificationsEndpoint() throws InterruptedException, URISyntaxException {
        Map<UUID, SseEmitter> emitters = new HashMap<>();
        SseEmitter emitter = new SseEmitter();
        UUID uuid = UUID.randomUUID();
        emitters.put(uuid, emitter);
        CountDownLatch latch = new CountDownLatch(1);

        String url = "http://localhost:"+port+"/api/notification/notifications/"+uuid.toString();
        MockRestServiceServer server = MockRestServiceServer.createServer(testRestTemplate.getRestTemplate());

        server.expect(ExpectedCount.once(),
                        requestTo(url))
                .andRespond(withSuccess("sample event", MediaType.TEXT_EVENT_STREAM));
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(url, String.class);
        String responseBody = responseEntity.getBody();
        System.out.println("Received SSE event: " + responseBody);

        responseEntity.getBody();
        latch.countDown();
        boolean receivedEvent = latch.await(5, TimeUnit.SECONDS);
        if (!receivedEvent) {
            throw new AssertionError("SSE event haven't been returned in 5 seconds.");
        }
    }
}