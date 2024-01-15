//package com.example.springwebsocket.controller;
//
//import com.example.springwebsocket.domain.Member;
//import com.example.springwebsocket.repository.MemberRepository;
//import com.example.springwebsocket.session.SessionConst;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//@RequestMapping("/members")
//public class MemberController {
//
//    private final MemberRepository memberRepository;
//
//    /**
//     * 회원가입 폼 제공
//     */
//    @GetMapping("/add")
//    public String add() {
//        return "members/addMemberForm";
//    }
//
//    @PostMapping("/add")
//    public String createMember(@RequestParam String username,
//                               @RequestParam String loginId,
//                               @RequestParam String password) {
//        Member member = new Member(username, loginId, password);
//        memberRepository.save(member);
//
//        return "redirect:/";
//    }
//
//    /**
//     * login post 전송처리
//     */
//    @PostMapping("/login")
//    public String loginMember(@RequestParam String loginId,
//                              @RequestParam String password,
//                              HttpServletRequest request) {
//        Member loginMember = memberRepository.findByLoginIdAndPassword(loginId, password);
//        if(loginMember == null) {
//            return "redirect:/";
//        }
//
////        Cookie cookie = new Cookie("username", loginMember.getUsername());
////        cookie.setDomain("localhost");
////        cookie.setPath("/");
////        // 30초간 저장
////        cookie.setMaxAge(30*60);
////        cookie.setSecure(true);
////        response.addCookie(cookie);
//
//        /**
//         * 로그인 성공
//         */
//        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
//        HttpSession session = request.getSession();
//        //세션에 로그인 회원 정보 보관
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//
//        return "redirect:/chat";
//    }
//}
