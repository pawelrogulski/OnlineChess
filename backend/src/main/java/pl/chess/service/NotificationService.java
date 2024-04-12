package pl.chess.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Service
public class NotificationService {
    private final Map<UUID,SseEmitter> emitters = new HashMap<>();

    public void putEmitter(UUID playerId, SseEmitter emitter){
        emitters.put(playerId, emitter);
    }
    public void sendToEmitter(String notification, UUID playerId) {
        SseEmitter emitter = emitters.get(playerId);
        try{
            emitter.send(notification, MediaType.TEXT_PLAIN);
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    public void removeEmitter(UUID playerId) {
        emitters.remove(playerId);
    }
}
