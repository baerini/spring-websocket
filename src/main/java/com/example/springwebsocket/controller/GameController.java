package com.example.springwebsocket.controller;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.domain.Member;
import com.example.springwebsocket.domain.dto.MemberDto;
import com.example.springwebsocket.security.SecurityService;
import com.example.springwebsocket.service.GameService;
import com.example.springwebsocket.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameController {
    private final GameService gameService;
    private final MemberService memberService;
    private final SecurityService securityService;

//    @GetMapping("/white")
//    public String whiteGame(Authentication authentication, Model model) {
//        Member member = (Member) authentication.getPrincipal();
//        MemberDto memberDto = new MemberDto(member.getUsername(), member.getRating(), member.getWin(), member.getLose());
//        model.addAttribute("member", memberDto);
//        Game game = gameService.createGame();
//        System.out.println("jwt = " + securityService.makeJwtToken());
//        return "game/gameWhite";
//    }
//
//    @GetMapping("/black")
//    public String blackGame(Authentication authentication, Model model) {
//        Member member = (Member) authentication.getPrincipal();
//        MemberDto memberDto = new MemberDto(member.getUsername(), member.getRating(), member.getWin(), member.getLose());
//        model.addAttribute("member", memberDto);
//        return "game/gameBlack";
//    }

    // /game/{gameId} 가 best
    @GetMapping("/game")
    public String gameStart(@RequestParam("gameId") Long gameId,
                            @RequestParam("color") String color,
                            @RequestParam("time") Long time,
                            Authentication authentication,
                            Model model) {
        Member member = (Member) authentication.getPrincipal();
        MemberDto memberDto = new MemberDto(member.getUsername(), member.getRating(), member.getWin(), member.getLose());

        if (color.equals("white")) {
            return "game/gameWhite";
        }
        return "game/gameBlack";
    }

    // game?gameId=1&color=black&time=15
    // game?gameId=1&color=white&time=15


    @GetMapping("/lobby")
    public String lobby(Authentication authentication, Model model) {
        Member member = (Member) authentication.getPrincipal();
        MemberDto memberDto = new MemberDto(member.getUsername(), member.getRating(), member.getWin(), member.getLose());
        model.addAttribute("member", memberDto);
        return "lobby";
    }

//    @GetMapping("/ranking")
//    public String ranking() {
//        return "game/lobby";
//    }
}
