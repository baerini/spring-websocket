package com.example.springwebsocket.domain;

import com.example.springwebsocket.service.ChatService;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Slf4j
public class ChatRoom {
    @Id @GeneratedValue
    private Long id;

    private String roomId; // 채팅방 아이디
    private String name; // 채팅방 이름

    @Transient
    private Set<WebSocketSession> sessions = new HashSet<>();

    public ChatRoom() {
    }

//    @Builder
    public ChatRoom(String roomId, String name){
        this.roomId = roomId;
        this.name = name;
    }

    public void handleAction(WebSocketSession session, ChatMessage chatMessage, ChatService service) {
        log.info("sessions={}", sessions);
        if (chatMessage.getType().equals(ChatMessage.MessageType.ENTER)) {
            sessions.add(session);

            chatMessage.setMessage(chatMessage.getSender() + " 님이 입장하셨습니다");
        }
        sendMessage(chatMessage, service);
    }

    public <T> void sendMessage(T message, ChatService service) {
        sessions.parallelStream().forEach(session -> service.sendMessage(session, message));
    }
}