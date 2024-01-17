package com.example.springwebsocket.controller;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.security.SecurityService;
import com.example.springwebsocket.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final SecurityService securityService;

    @GetMapping("/white")
    public String whiteGame() {
        Game game = gameService.createGame();
        System.out.println("jwt = " + securityService.makeJwtToken());
        return "game/gameWhite";
    }

    @GetMapping("/black")
    public String blackGame() {
        return "game/gameBlack";
    }
}
