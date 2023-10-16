package com.example.springwebsocket.handler;

import com.example.springwebsocket.dto.ChatDTO;
import com.example.springwebsocket.dto.ChatRoom;
import com.example.springwebsocket.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {
    // jackson의 objectMapper : Json <=> object
    private final ObjectMapper mapper;
    private final ChatService service;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("WebSocketSession {}", session);

        String payload = message.getPayload();
        log.info("payload {}", payload);

        // mapper.readValue => 자바입장에서 읽겠다 (객체로 변환하겠다) => json->object
        ChatDTO chatContent = mapper.readValue(payload, ChatDTO.class);
        log.info("session {}", chatContent.toString());

        ChatRoom room = service.findRoomById(chatContent.getRoomId());
        log.info("room {}", room.toString());
        room.handleAction(session, chatContent, service);
    }
}