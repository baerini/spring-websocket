//package com.example.springwebsocket.domain;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@Getter @Setter
//public class ChatMessage {
//    public enum MessageType {
//        ENTER, TALK, LEAVE
//    }
//
//    @Id @GeneratedValue
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    private MessageType type;
//    private String roomId;
//    private String sender;
//    private String message;
//
//    public ChatMessage() {
//    }
//}
