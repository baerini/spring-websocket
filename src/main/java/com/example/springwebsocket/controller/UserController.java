package com.example.springwebsocket.controller;

import com.example.springwebsocket.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok().body(userService.login("", ""));
    }

    @PostMapping("/reviews")
    public ResponseEntity<String> writeReview() {
        return ResponseEntity.ok().body("리뷰 등록");
    }
}
