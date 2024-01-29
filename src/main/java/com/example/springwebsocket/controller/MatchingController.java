package com.example.springwebsocket.controller;

import com.example.springwebsocket.domain.Matching;
import com.example.springwebsocket.domain.dto.MatchingDto;
import com.example.springwebsocket.service.MatchingService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingService matchingService;

    @GetMapping("/hello")
    public Result hello() {
        ArrayList<Matching> matchSessions = matchingService.getMatchSessions();

        List<MatchingDto> collect = matchSessions.stream()
                .map(m-> new MatchingDto(m.getMember(), m.getTime(), m.getWaiting()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data; // 리스트의 값
    }
}

