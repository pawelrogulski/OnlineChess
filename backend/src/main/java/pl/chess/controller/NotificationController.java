package pl.chess.controller;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.chess.service.NotificationService;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/notification")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/notifications/{playerId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getNotifications(@PathVariable String playerId) {
        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> { // disconnect handler
            notificationService.removeEmitter(UUID.fromString(playerId));
        });
        notificationService.putEmitter(UUID.fromString(playerId), emitter);
        return emitter;
    }
}
