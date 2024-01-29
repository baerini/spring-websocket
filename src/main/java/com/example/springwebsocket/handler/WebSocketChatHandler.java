package com.example.springwebsocket.handler;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.domain.Matching;
import com.example.springwebsocket.domain.Payload;
import com.example.springwebsocket.domain.dto.MemberDto;
import com.example.springwebsocket.service.GameService;
import com.example.springwebsocket.service.MatchingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {
    /**
     *
     *  type : MATCH
     *      gameId : 0
     *      who : username
     *      type : MATCH
     *      message : unnecessary
     *  type : MATCHED
     *      gameId : variable
     *      who : unnecessary
     *      type : MATCHED
     *      message : color|time
     *  type : ENTER
     *      gameId : variable
     *      who : white, black
     *      type : ENTER
     *      message : unnecessary
     *  type : CHAT
     *      gameId : variable
     *      who : white, black
     *      type : CHAT
     *      message : message ex) 안녕
     *  type : MOVE
     *      gameId : variable
     *      who : white, black
     *      type : MOVE
     *      message : from - to  ex) a8|h3
     *  type : RESULT
     *      gameId : variable
     *      who : white, black (둘 중 하나 선택)
     *      type : RESULT
     *      message: winner - loser ex) white-win|black-lose
     *
     */

    // jackson의 objectMapper : Json <=> object
    private final ObjectMapper mapper;
    private final GameService gameService; // matchingService로 옮기기
    private final MatchingService matchingService;

//    private Set<WebSocketSession> matchSessions = new HashSet<>();
//    private final ChatService service;
//    private final ChatMessageRepository chatMessageRepository;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        log.info("WebSocketSession : {}", session);

//        String payload = message.getPayload();

        System.out.println("session = " + session);

        Payload payload = mapper.readValue(message.getPayload(), Payload.class);
        log.info("payload : {}", payload.toString());

        //추후에 Game game = gameService.findRoomById(paylaod.gameId)로 변경;

        if(payload.type.equals(Payload.PayloadType.MATCH)) { // MATCHED 받을 일이 없음
            ArrayList<Matching> matchingSessions = matchingService.getMatchSessions();
            MemberDto member = mapper.readValue(payload.message, MemberDto.class);
            Long time = payload.gameId;

            matchingSessions.add(new Matching(session, member, time, 0));
            log.info("matchSessions = {}", matchingSessions);

            // session.send(대기열 객체 리스트) => api로 받고
            // add(session)
            // sendToEachSocket(session 정보 객체)

            sendToEachSocket(matchingSessions, message);
            log.info("message = {}", message);
        } else {
            Game game = gameService.findById(1L);
            log.info("game : {}", game);

            Set<WebSocketSession> sessions = game.getMatchedSessions();

            if(payload.type.equals(Payload.PayloadType.ENTER)) {

                sessions.add(session);
            } else if(payload.type.equals(Payload.PayloadType.MOVE)) {

            } else if(payload.type.equals(Payload.PayloadType.CHAT)) {

            } else if(payload.type.equals(Payload.PayloadType.RESULT)) {

            }
            sendToEachSocket(sessions, message);
        }

//
//        if (chatMessage.getType().equals(ChatMessage.MessageType.ENTER)) {
//            sessions.add(session);
//            chatMessage.setMessage(chatMessage.getSender() + " 님이 입장했습니다.");
////            sendToEachSocket(sessions,new TextMessage(mapper.writeValueAsString(chatMessage)) );
//
//        } else if (chatMessage.getType().equals(ChatMessage.MessageType.LEAVE)) {
//            sessions.remove(session);
//            chatMessage.setMessage(chatMessage.getSender() + " 님이 퇴장했습니다.");
////            sendToEachSocket(sessions,new TextMessage(mapper.writeValueAsString(chatMessage)) );
//
//        }
//        sendToEachSocket(sessions, new TextMessage(mapper.writeValueAsString(chatMessage)) );
//        chatMessageRepository.save(chatMessage);
    }

    private void sendToEachSocket(Set<WebSocketSession> sessions, TextMessage message){
        System.out.println("sessions = " + sessions);

        sessions.parallelStream().forEach( gameSession -> {
            try {
                gameSession.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // overloading
    private void sendToEachSocket(ArrayList<Matching> sessions, TextMessage message){
        System.out.println("matchSessions = " + sessions);

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