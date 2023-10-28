package com.example.springwebsocket.controller;

import com.example.springwebsocket.domain.ChatRoom;
import com.example.springwebsocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    /**
     * entity 현재 그대로 넘기고있음
     */
    @GetMapping
    public String chatRooms(Model model) {
        // 게시글 리스트 페이지
        List<ChatRoom> chatRooms = chatService.findAllRoom();
        model.addAttribute("chatRooms", chatRooms);

        return "chat/chatRooms";
    }

    @GetMapping("/add")
    public String chatAddForm() {
        return "chat/chatAddForm";
    }

    @PostMapping("/create")
    public String chatCreate(@RequestParam String name) {
        chatService.createRoom(name);
        return "redirect:/chat";
    }

    @GetMapping("/{name}")
    public String chatRoom(@PathVariable("name") String name, Model model) {
//        model.addAttribute("name", name);
        return "chat/chatRoom";
    }

//    @GetMapping
//    public List<ChatRoom> findAllRoom() {
//        return chatService.findAllRoom();
//    }
}