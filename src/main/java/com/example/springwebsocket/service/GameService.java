package com.example.springwebsocket.service;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.repository.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Data
@Service
@RequiredArgsConstructor
public class GameService {
    private ObjectMapper mapper;
    private Map<Long, Game> games;
    private final GameRepository gameRepository;
//    private final ChatRoomRepository chatRoomRepository;

    @PostConstruct
    private void init() {
        games = new LinkedHashMap<>();
        mapper = new ObjectMapper();
    }

    public List<Game> findAllRoom(){
        return new ArrayList<>(games.values());
    }

    public Game findById(Long gameId){
        return games.get(gameId);
    }
//
//    public Game findRoomByName(String name) {
//        return chatRoomRepository.findByName(name);
//    }

    public Game createGame() {
//        String roomId = UUID.randomUUID().toString(); // 랜덤한 방 아이디 생성

        // Builder 를 이용해서 ChatRoom 을 Building
//        ChatRoom room = ChatRoom.builder()
//                .roomId(roomId)
//                .name(name)
//                .build();
//        Game room = new Game(roomId, name);
        Game game = new Game();
        gameRepository.save(game);
        Long gameId = game.getId();
        log.info("create game : {}", game);
//        chatRoomRepository.save(room);

        games.put(gameId, game); // 랜덤 아이디와 room 정보를 Map 에 저장
        return game;
    }

//    public <T> void sendMessage(WebSocketSession session, T message) {
//        try{
//            //mapper.writeValueAsString(객체) => json 문자열로 반환
//            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//        }
//    }
}
