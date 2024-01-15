package com.example.springwebsocket.domain;

import lombok.ToString;

@ToString
public class Payload {
    public enum PayloadType {
        ENTER, MOVE, CHAT, RESULT
    }
    public Long gameId;
    public String sender;
    public PayloadType type;
    public String message;
}
