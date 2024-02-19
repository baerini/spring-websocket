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

    @GetMapping("/game")
    public String gameStart(@RequestParam("gameId") Long gameId,
                            Authentication authentication,
                            Model model) {
        model.addAttribute("gameId", gameId);

        Member member = (Member) authentication.getPrincipal();
        MemberDto member1 = new MemberDto(member.getUsername(), member.getRating(), member.getWin(), member.getLose());
        model.addAttribute("member1", member1);

        Game game = gameService.findById(gameId);
        Long time = game.getTime();

        model.addAttribute("time", game.getTime());
        String whiteUsername = game.getWhite();

        if (member.getUsername().equals(whiteUsername)) {
            Member blackMember = memberService.findByUsername(game.getBlack());
            MemberDto member2 = new MemberDto(blackMember.getUsername(), blackMember.getRating(), blackMember.getWin(), blackMember.getLose());

            model.addAttribute("member2", member2);
            return "game/gameWhite";
        } else {
            Member whiteMember = memberService.findByUsername(whiteUsername);
            MemberDto member2 = new MemberDto(whiteMember.getUsername(), whiteMember.getRating(), whiteMember.getWin(), whiteMember.getLose());

            model.addAttribute("member2", member2);
            return "game/gameBlack";
        }
    }

    @GetMapping("/lobby")
    public String lobby(Authentication authentication, Model model) {
        Member member = (Member) authentication.getPrincipal();
        MemberDto memberDto = new MemberDto(member.getUsername(), member.getRating(), member.getWin(), member.getLose());
        model.addAttribute("member", memberDto);
        return "lobby";
    }
}
