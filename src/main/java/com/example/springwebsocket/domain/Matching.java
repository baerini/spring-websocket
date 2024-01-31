package com.example.springwebsocket.domain;

import com.example.springwebsocket.domain.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Matching {
    private WebSocketSession session;
    private MemberDto member;
    private Long time;
    private Date waiting;
    private boolean state;

    public void setState() {
        this.state = true;
    }
}
