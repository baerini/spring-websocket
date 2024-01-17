package com.example.springwebsocket.controller;

import com.example.springwebsocket.domain.Member;
import com.example.springwebsocket.repository.MemberRepository;
import com.example.springwebsocket.service.MemberService;
import com.example.springwebsocket.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "members/loginMemberForm";
    }

    @PostMapping("/login")
    public String createMember(@RequestParam String loginId,
                               @RequestParam String password,
                               HttpServletResponse response) {
        Member member = memberRepository.findByLoginIdAndPassword(loginId, password);
        String jwt = userService.login(loginId, "");
        log.info("발급할 jwt = {}", jwt);

        Cookie cookie = new Cookie("jwt-token", jwt);
        response.addCookie(cookie);

        return "redirect:/members/lobby";
    }

    @GetMapping("/lobby")
    public String lobby() {
        return "lobby";
    }

    @GetMapping("/add")
    public String add() {
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String createMember(@RequestParam String username,
                               @RequestParam String loginId,
                               @RequestParam String password) {
        Member member = new Member(username, loginId, password);
        memberRepository.save(member);

        return "redirect:/members/login";
    }
}
