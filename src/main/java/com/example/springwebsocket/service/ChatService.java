//package com.example.springwebsocket.service;
//
//import com.example.springwebsocket.domain.ChatRoom;
//import com.example.springwebsocket.repository.ChatRoomRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.PostConstruct;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.io.IOException;
//import java.util.*;
//
//@Slf4j
//@Data
//@Service
//@RequiredArgsConstructor
//public class ChatService {
//    private ObjectMapper mapper;
//    private Map<String, ChatRoom> chatRooms;
//    private final ChatRoomRepository chatRoomRepository;
//
//    @PostConstruct
//    private void init() {
//        chatRooms = new LinkedHashMap<>();
//        mapper = new ObjectMapper();
//    }
//
//    public List<ChatRoom> findAllRoom(){
//        return new ArrayList<>(chatRooms.values());
//    }
//
//    public ChatRoom findRoomById(String roomId){
//        return chatRooms.get(roomId);
//    }
//
//    public ChatRoom findRoomByName(String name) {
//        return chatRoomRepository.findByName(name);
//    }
//
//    public ChatRoom createRoom(String name) {
//        String roomId = UUID.randomUUID().toString(); // 랜덤한 방 아이디 생성
//
//        // Builder 를 이용해서 ChatRoom 을 Building
////        ChatRoom room = ChatRoom.builder()
////                .roomId(roomId)
////                .name(name)
////                .build();
//        ChatRoom room = new ChatRoom(roomId, name);
//        chatRoomRepository.save(room);
//
//        chatRooms.put(roomId, room); // 랜덤 아이디와 room 정보를 Map 에 저장
//        return room;
//    }
//
////    public <T> void sendMessage(WebSocketSession session, T message) {
////        try{
////            //mapper.writeValueAsString(객체) => json 문자열로 반환
////            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
////        } catch (IOException e) {
////            log.error(e.getMessage(), e);
////        }
////    }
//}
