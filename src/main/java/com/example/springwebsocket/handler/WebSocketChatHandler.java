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
import java.util.*;

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

//        System.out.println("session = " + session);

        Payload payload = mapper.readValue(message.getPayload(), Payload.class);
//        log.info("payload : {}", payload.toString());

        //추후에 Game game = gameService.findRoomById(paylaod.gameId)로 변경;

        if(payload.type.equals(Payload.PayloadType.MATCH)) { // MATCHED 받을 일이 없음
            MemberDto member = mapper.readValue(payload.message, MemberDto.class);
            Long time = payload.gameId;

            Matching matching = new Matching(session, member, time, new Date(), false);


            /***
             * 매칭버튼 =>
             * 프론트 현재 matchingSessions ajax로 받아오고 테이블에 추가
             * 백엔드 자신을 matchingSessions 에 추가
             * 백엔드 자신이 들어왔다는 것을 matchingSessions 에게 알림
             * 프론트 테이블 표 하위에 알림받은 사람을 추가(자신이 될수도 있음)
             *
             */

            //1.
            matchingService.getMatchingSessions().add(matching);
            sendToEachSocket(matchingService.getMatchingSessions(), message);
            // 대기열 추가 send : sendToEachSocket(matchingService.getMatchingSessions(), message);
            // 매칭 완료 알림 send : sendToEachSocket(matchingService.getMatchedSessions(), message);

            //2.

//            log.info("message = {}", message);
        } else {
            Map<Long, Set<WebSocketSession>> map = gameService.getMap();

            Game game = gameService.findById(payload.gameId);
            Set<WebSocketSession> sessions = map.get(payload.gameId);

            if (payload.type.equals(Payload.PayloadType.ENTER)) {
                sessions.add(session);
            } else if (payload.type.equals(Payload.PayloadType.MOVE)) {

            } else if (payload.type.equals(Payload.PayloadType.CHAT)) {

            } else if (payload.type.equals(Payload.PayloadType.RESULT)) {

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
//        System.out.println("sessions = " + sessions);

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
//        System.out.println("matchSessions = " + sessions);

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