package com.pinta.lounge.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class MessageHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> broadcast = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Message Connection");
        if (Objects.nonNull(session.getPrincipal()) && StringUtils.hasText(session.getPrincipal().getName())) {
            String name = session.getPrincipal().getName();
            broadcast.put(name, session);
            log.info("Message Subscription: {}", name);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("Message Payload: {}", message.getPayload().replaceAll("[\r\n]", ""));
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        log.info("Message Disconnection: {}", status);
        if (Objects.nonNull(session.getPrincipal()) && StringUtils.hasText(session.getPrincipal().getName())) {
            String name = session.getPrincipal().getName();
            log.info("Message Unsubscribe: {}", name);
            broadcast.remove(name).close();
        }
    }

}
