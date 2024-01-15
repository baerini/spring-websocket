package com.example.springwebsocket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Slf4j
public class Game {

    @Id @GeneratedValue
    private Long id;

    @Transient
    private Set<WebSocketSession> sessions = new HashSet<>();

    public Game() {}
}
