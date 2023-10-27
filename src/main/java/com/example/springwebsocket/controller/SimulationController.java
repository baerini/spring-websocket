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
public class SimulationController {
    private final ChatService chatService;

    @GetMapping("/chat")
    public String chatRooms(Model model) {
        // 게시글 리스트 페이지
        List<ChatRoom> chatRooms = chatService.findAllRoom();
        model.addAttribute("chatRooms", chatRooms);

        return "chatRooms";
    }

    @GetMapping("add")
    public String chatAddForm() {return "chatAddForm";}

    @GetMapping("/chat/{roomId}")
    public String chatRoom(@PathVariable("roomId") String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "chatRoom";
    }

    @PostMapping("/create")
    public String chatCreate(@RequestBody String name) {
        chatService.createRoom(name);
        return "redirect:/chat";
    }
}
