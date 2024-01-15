package com.example.springwebsocket.handler;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.domain.Payload;
import com.example.springwebsocket.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {
    /**
     *
     *  gameId : Long
     *  who : white, black
     *  type : ENTER, chat, move, gameResult
     *  message : move => from - to  ex) a8|h3  // 프론트엔드 영역
     *            chat => message ex) 안녕  // 프론트엔드 영역
     *            gameResult => winner - loser ex) white-win|black-lose // 백엔드에서 처리
     */

    // jackson의 objectMapper : Json <=> object
    private final ObjectMapper mapper;
    private final GameService gameService;
//    private final ChatService service;
//    private final ChatMessageRepository chatMessageRepository;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        log.info("WebSocketSession : {}", session);

//        String payload = message.getPayload();

        System.out.println("session = " + session);

        Payload payload = mapper.readValue(message.getPayload(), Payload.class);
        log.info("payload : {}", payload.toString());
//        ChatMessage chatMessage = mapper.readValue(payload, ChatMessage.class);
//        log.info("session : {}", chatMessage.toString());

//        ChatRoom room = service.findRoomById(chatMessage.getRoomId());
//        log.info("room : {}", room.toString());

        //추후에 Game game = gameService.findRoomById(paylaod.gameId)로 변경;
        Game game = gameService.findRoomById(1L);
        log.info("game : {}", game);

        Set<WebSocketSession> sessions = game.getSessions();

        if(payload.type.equals(Payload.PayloadType.ENTER)) {
            sessions.add(session);
        } else if(payload.type.equals(Payload.PayloadType.MOVE)) {

        } else if(payload.type.equals(Payload.PayloadType.CHAT)) {

        } else if(payload.type.equals(Payload.PayloadType.RESULT)) {

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
        sendToEachSocket(sessions, message);
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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }

}