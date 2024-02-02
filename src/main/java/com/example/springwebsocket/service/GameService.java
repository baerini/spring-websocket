package com.example.springwebsocket.service;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.repository.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

@Slf4j
@Data
@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final Map<Long, Set<WebSocketSession>> map = new HashMap<>();

    public Game findById(Long gameId){
        return gameRepository.findById(gameId).get();
    }

    public Game createGame(String white, String black, Long time) {
        Game game = new Game(white, black, time);
        gameRepository.save(game);
        Long gameId = game.getId();
        log.info("create game : {}", game);

        return game;
    }
}
