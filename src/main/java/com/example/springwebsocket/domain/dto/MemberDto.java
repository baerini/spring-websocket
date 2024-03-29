package com.example.springwebsocket.domain.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private String username;
    private int rating;
    private int win;
    private int lose;
}
