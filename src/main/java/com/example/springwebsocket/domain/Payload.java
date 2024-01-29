package com.example.springwebsocket.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Payload {
    public enum PayloadType {
        MATCHED, MATCH, ENTER, MOVE, CHAT, RESULT
    }
    public Long gameId;
    public String sender;
    public PayloadType type;
    public String message;
}
