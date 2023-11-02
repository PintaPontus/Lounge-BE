package com.pinta.lounge.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinta.lounge.dto.MessageIn;
import com.pinta.lounge.dto.MessageOut;
import com.pinta.lounge.security.SecurityUtils;
import com.pinta.lounge.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class MessageHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> broadcastSessions = new HashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageService messageService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Message Connection");
        if (Objects.nonNull(session.getPrincipal()) && StringUtils.hasText(session.getPrincipal().getName())) {
            Long userId = SecurityUtils.getIdFromSession(session);
            broadcastSessions.put(userId, session);
            log.info("Message Subscription: {}", userId);
        }
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws IOException {
        log.info("Message Payload: {}", message.getPayload().replaceAll("[\r\n]", ""));
        MessageIn msgArrived = objectMapper.readValue(message.getPayload(), MessageIn.class);

        Long userId = SecurityUtils.getIdFromSession(session);

        messageService.sendMessage(msgArrived, userId);

        MessageOut msgToSend = new MessageOut()
            .setUserId(userId)
            .setContent(msgArrived.getContent())
            .setTime(LocalDateTime.now());

        TextMessage textToSend = new TextMessage(objectMapper.writeValueAsBytes(msgToSend));

        messageService.getParticipantsId(msgArrived.getChatId())
            .forEach(uid -> Optional.ofNullable(broadcastSessions.get(uid)).ifPresent(s -> {
                    try {
                        s.sendMessage(textToSend);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                })
            );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        log.info("Message Disconnection: {}", status);
        if (Objects.nonNull(session.getPrincipal()) && StringUtils.hasText(session.getPrincipal().getName())) {
            Long userId = SecurityUtils.getIdFromSession(session);
            log.info("Message Unsubscribe: {}", userId);
            broadcastSessions.remove(userId).close();
        }
    }

}
