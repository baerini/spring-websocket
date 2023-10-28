package com.example.springwebsocket.controller;

import com.example.springwebsocket.domain.Member;
import com.example.springwebsocket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
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

    /**
     * login post 전송처리
     */
    @PostMapping("/login")
    public String loginMember(@RequestParam String loginId,
                              @RequestParam String password) {
        List<Member> members = memberRepository.findByLoginIdAndPassword(loginId, password);
        if(members.size() == 1) {
            return "chatRooms";
        }
        return "redirect:/";
    }
}
