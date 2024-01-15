//package com.example.springwebsocket.controller;
//
//import com.example.springwebsocket.domain.ChatRoom;
//import com.example.springwebsocket.domain.Member;
//import com.example.springwebsocket.repository.MemberRepository;
//import com.example.springwebsocket.service.ChatService;
//import com.example.springwebsocket.session.SessionConst;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/chat")
//public class ChatController {
//    private final ChatService chatService;
//    private final MemberRepository memberRepository;
//
//    /**
//     * entity 현재 그대로 넘기고있음
//     */
//    @GetMapping
//    public String chatRooms(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member loginMember, Model model) {
//        // 게시글 리스트 페이지
//        List<ChatRoom> chatRooms = chatService.findAllRoom();
//        model.addAttribute("chatRooms", chatRooms);
//        model.addAttribute("username", loginMember.getUsername());
//
//        return "chat/chatRooms";
//    }
//
//    @GetMapping("/add")
//    public String chatAddForm() {
//        return "chat/chatAddForm";
//    }
//
//    @PostMapping("/create")
//    public String chatCreate(@RequestParam String name) {
//        chatService.createRoom(name);
//        return "redirect:/chat";
//    }
//
//    /**
//     * 방에 roomId 보내줘야함 javascript에서 사용할거
//     */
//    @GetMapping("/{name}")
//    public String chatRoom(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member loginMember,
//                           @PathVariable("name") String name, Model model) {
//        ChatRoom chatRoom = chatService.findRoomByName(name);
//        model.addAttribute("roomId", chatRoom.getRoomId());
//        model.addAttribute("username", loginMember.getUsername());
//        return "chat/chatRoom";
//    }
//}