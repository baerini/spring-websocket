package com.example.springwebsocket.service;

import com.example.springwebsocket.repository.MemberRepository;
import com.example.springwebsocket.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    public String login(String username, String password) {
        Long expiredMs = 1000 * 60 * 60l;
        return JwtUtil.createJwt(username, secretKey, expiredMs);
    }
}
