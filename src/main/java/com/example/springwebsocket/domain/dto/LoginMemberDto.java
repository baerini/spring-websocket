package com.example.springwebsocket.domain.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginMemberDto {
    private String loginId;
    private String password;
}
