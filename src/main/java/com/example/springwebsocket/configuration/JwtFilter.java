package com.example.springwebsocket.configuration;

import com.example.springwebsocket.service.UserService;
import com.example.springwebsocket.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        String authorization = "";
        Cookie[] cookies = request.getCookies();

        for(Cookie c : cookies) {
            if(c.getName().equals("jwt-token")) {
                authorization = c.getValue();
            }
        }

        log.info("authorization = {}", authorization);

        //token 없으면 block
//        if (authorization == null || !authorization.startsWith("Bearer ")) {
//            log.error("authorization 이 없습니다.");
//            filterChain.doFilter(request, response);
//            return;
//        }

        //token 꺼내기
//        String token = authorization.split(" ")[1];
        String token = authorization;

        // token expired 여부
        if(JwtUtil.isExpired(token, secretKey)) {
            log.error("token 만료 되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }


        //token에서 username꺼내기
        String username = JwtUtil.getUsername(token, secretKey);
        log.info("userName = {}", username);

        //권한 부여
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("USER")));

        //detail
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
