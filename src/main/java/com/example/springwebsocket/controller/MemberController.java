package com.example.springwebsocket.controller;

import com.example.springwebsocket.domain.Member;
import com.example.springwebsocket.domain.dto.AddMemberDto;
import com.example.springwebsocket.domain.dto.LoginMemberDto;
import com.example.springwebsocket.repository.MemberRepository;
import com.example.springwebsocket.service.MemberService;
import com.example.springwebsocket.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginMemberDto", new LoginMemberDto());
        return "members/loginMemberForm";
    }

    @PostMapping("/login")
    public String createMember(@ModelAttribute LoginMemberDto loginMemberDto,
                               HttpServletResponse response,
                               Model model) {
        Member member = memberRepository.findByLoginIdAndPassword(loginMemberDto.getLoginId(), loginMemberDto.getPassword());
        Map<String, String> errors = new HashMap<>();

        if(member == null) {
            errors.put("notSignUp", "유효하지 않은 아이디/비밀번호 입니다.");
        }

        if(!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "members/loginMemberForm";
        }

        String jwt = userService.login(member.getUsername(), "");

        Cookie cookie = new Cookie("jwt", "Bearer+" + jwt);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return "redirect:/lobby";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("addMemberDto", new AddMemberDto());
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String createMember(@ModelAttribute AddMemberDto addMemberDto, RedirectAttributes redirectAttributes, Model model) {
        Map<String, String> errors = new HashMap<>();

        if (!StringUtils.hasText(addMemberDto.getLoginId())) {
            errors.put("loginId", "아이디를 입력하세요.");
        }

        if (!StringUtils.hasText(addMemberDto.getPassword())) {
            errors.put("password", "비밀번호를 입력하세요.");
        }

        if (!StringUtils.hasText(addMemberDto.getUsername())) {
            errors.put("username", "이름을 입력하세요.");
        }

        if(!(memberRepository.findByLoginId(addMemberDto.getLoginId()) == null)) {
            errors.put("loginId", "이미 가입되어 있는 아이디입니다.");
        }

        if(!(memberRepository.findByUsername(addMemberDto.getUsername()) == null)) {
            errors.put("username", "이미 가입되어 있는 이름입니다.");
        }

        if(!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "members/addMemberForm";
        }

        Member member = new Member(addMemberDto.getLoginId(), addMemberDto.getPassword(), addMemberDto.getUsername());
        memberRepository.save(member);

        return "redirect:/members/login";
    }
}
