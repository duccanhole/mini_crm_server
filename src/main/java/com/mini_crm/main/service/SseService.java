package com.mini_crm.main.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SseService.class);
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        String key = userId.toString();
        logger.info("New SSE subscription request for user: {}", key);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("SSE session started for user: " + key));
        } catch (IOException e) {
            logger.error("Failed to send initial SSE event for user: {}", key, e);
            return null;
        }

        emitters.put(key, emitter);

        emitter.onCompletion(() -> {
            logger.info("SSE connection completed for user: {}", key);
            emitters.remove(key);
        });
        emitter.onTimeout(() -> {
            logger.warn("SSE connection timed out for user: {}", key);
            emitters.remove(key);
        });
        emitter.onError(e -> {
            logger.error("SSE connection error for user: {}", key, e);
            emitters.remove(key);
        });

        return emitter;
    }

    public void emit(String userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                logger.info("Emitting SSE event to user {}: {}", userId, data);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(data));
            } catch (IOException e) {
                logger.error("Failed to emit SSE event to user {}", userId, e);
                emitters.remove(userId);
            }
        }
    }
}
