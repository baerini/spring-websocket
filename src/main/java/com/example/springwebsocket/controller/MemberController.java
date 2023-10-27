package com.example.springwebsocket.controller;

import com.example.springwebsocket.domain.Member;
import com.example.springwebsocket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;

    /**
     * 회원가입 폼 제공
     */
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

        return "redirect:/";
    }
}
