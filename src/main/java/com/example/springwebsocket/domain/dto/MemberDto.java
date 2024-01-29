package com.example.springwebsocket.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
//    private Long id;
//    private String loginId;
//    private String password;
    private String username;
    private int rating;
    private int win;
    private int lose;
}
