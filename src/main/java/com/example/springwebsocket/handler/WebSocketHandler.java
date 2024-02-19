package com.example.springwebsocket.handler;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.domain.Matching;
import com.example.springwebsocket.domain.Payload;
import com.example.springwebsocket.domain.dto.MemberDto;
import com.example.springwebsocket.service.GameService;
import com.example.springwebsocket.service.MatchingService;
import com.example.springwebsocket.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final GameService gameService;
    private final MatchingService matchingService;
    private final RatingService ratingService;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Payload payload = mapper.readValue(message.getPayload(), Payload.class);

        if(payload.type.equals(Payload.PayloadType.MATCH)) {
            MemberDto member = mapper.readValue(payload.message, MemberDto.class);
            Long time = payload.gameId;

            Matching matching = new Matching(session, member, time, new Date(), false);

            matchingService.getMatchingSessions().add(matching);
            log.info("sessions={}", matchingService.getMatchingSessions());
            sendToEachSocket(matchingService.getMatchingSessions(), message);
        } else {
            Map<Long, Set<WebSocketSession>> map = gameService.getMap();
            Set<WebSocketSession> sessions = map.get(payload.gameId);
            log.info("enum type={}", payload.type);

            if (payload.type.equals(Payload.PayloadType.ENTER)) {
                if (sessions == null) {
                    sessions = new HashSet<>();
                    map.put(payload.gameId, sessions);
                }
                sessions.add(session);
            } else if (payload.type.equals(Payload.PayloadType.MOVE)) {
                sendToEachSocket(sessions, message);
            } else if (payload.type.equals(Payload.PayloadType.CHAT)) {
                sendToEachSocket(sessions, message);
            } else if (payload.type.equals(Payload.PayloadType.RESULT)) {
                Game game = gameService.findById(payload.gameId);
                if(!game.isFinish()) {
                    ratingService.renew(payload.gameId, payload.message);
                }
                map.remove(payload.gameId);
            }
        }
    }

    private void sendToEachSocket(Set<WebSocketSession> sessions, TextMessage message){
        sessions.parallelStream().forEach( gameSession -> {
            try {
                gameSession.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void sendToEachSocket(ArrayList<Matching> sessions, TextMessage message){
        sessions.parallelStream().forEach( gameSession -> {
            try {
                gameSession.getSession().sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }

}