package com.example.springwebsocket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Slf4j
@ToString
public class Game {

    @Id @GeneratedValue
    private Long id;

    /**
     * 초기 세팅
     */
    private Long time;
    private String white;
    private String black;

    /**
     * 종료 후 수정 쿼리
     */
    private String winner;
    private String loser;
    private boolean finish;

//    @Transient //길이 최대 2 제약
//    private Set<WebSocketSession> playingSessions = new HashSet<>();

    public Game() {}

    /**
     * 초기 세팅
     */
    public Game(String white, String black, Long time) {
        this.white = white;
        this.black = black;
        this.time = time;
    }
}
