package com.example.springwebsocket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
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

    public Game() {}

    /**
     * 초기 세팅
     */
    public Game(String white, String black, Long time) {
        this.white = white;
        this.black = black;
        this.time = time;
    }

    /**
     * 종료 후 수정 쿼리
     */
    public void finishGame(String winner, String loser, boolean finish) {
        this.winner = winner;
        this.loser = loser;
        this.finish = true;
    }
}
