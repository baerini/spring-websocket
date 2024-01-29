package com.example.springwebsocket.configuration;

import com.example.springwebsocket.repository.MemberRepository;
import com.example.springwebsocket.service.MemberService;
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
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final MemberService memberService;
    private final String secretKey;

    //https://byungil.tistory.com/294
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
//        String tmp = "";
        log.info("doFilterInternal()");
        Cookie[] cookies = request.getCookies();

        if (ObjectUtils.isEmpty(cookies)) {
            log.error("쿠키가 없습니다");
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = "";

        for(Cookie c : cookies) {
            if(c.getName().equals("jwt")) {
                authorization = c.getValue().replace("+", " ");
            }
        }

        log.info("authorization = {}", authorization);

        //token 없으면 block || !authorization.startsWith("Bearer ")
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("authorization 이 없습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        //token 꺼내기 .split(" ")[1]
        String token = authorization.split(" ")[1];
//        String token = authorization;

        // token expired 여부
        if(JwtUtil.isExpired(token, secretKey)) {
            log.error("token 만료 되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }


        //token 에서 username 꺼내기
        String username = JwtUtil.getUsername(token, secretKey);
        log.info("username = {}", username);

        //권한 부여
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberService.findByUsername(username), null, List.of(new SimpleGrantedAuthority("USER")));
        log.info("UsernamePasswordAuthenticationToken = {}", authenticationToken);

        //detail
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
