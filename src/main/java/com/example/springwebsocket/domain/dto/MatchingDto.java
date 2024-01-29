package com.example.springwebsocket.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.socket.WebSocketSession;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MatchingDto {
//    private WebSocketSession session;
    private MemberDto member;
    private Long time;
    private int waiting;
}
