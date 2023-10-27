package com.example.springwebsocket.domain;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

//@Data
//@Builder
@Getter @Setter
public class ChatMessage {
    public enum MessageType {
        ENTER, TALK
    }

    @Enumerated(EnumType.STRING)
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
//    private String time;
}
