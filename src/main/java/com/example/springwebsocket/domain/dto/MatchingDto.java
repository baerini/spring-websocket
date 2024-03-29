package com.example.springwebsocket.domain.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchingDto {
    private MemberDto member;
    private Long time;
}
