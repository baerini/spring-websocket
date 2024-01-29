package com.example.springwebsocket.service;

import com.example.springwebsocket.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Value("${jwt.secret}")
    private String secretKey;

    private Long expiredMs = 1000 * 60 * 60l;

    public String login(String username, String password) {
        return JwtUtil.createJwt(username, secretKey, expiredMs);
    }
}
